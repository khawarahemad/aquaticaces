package com.aquaticaces.ui

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import net.minecraft.client.Minecraft

/**
 * High-performance, modern dual-pass Gaussian blur post-processing engine.
 * Fully compatible with OpenGL Core Profiles.
 */
class ShaderManager {
    private var programId = 0
    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    // Geometry handles
    private var quadVao = 0
    private var quadVbo = 0

    // Framebuffer mappings
    private var fboId = 0
    private var fboTextureId = 0
    private var lastWidth = 0
    private var lastHeight = 0

    private val vertexShaderSource = """
        #version 150
        in vec2 position;
        in vec2 uv;
        out vec2 fragTexCoord;
        void main() {
            fragTexCoord = uv;
            gl_Position = vec4(position, 0.0, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderSource = """
        #version 150
        uniform sampler2D u_texture;
        uniform vec2 u_dir;
        uniform float u_radius;
        uniform vec2 u_resolution;
        in vec2 fragTexCoord;
        out vec4 fragColor;
        
        void main() {
            vec4 sum = vec4(0.0);
            float totalWeight = 0.0;
            float sigma = max(u_radius / 2.0, 0.1);
            float twoSigmaSq = 2.0 * sigma * sigma;
            
            for (float i = -u_radius; i <= u_radius; i += 1.0) {
                float weight = exp(-(i * i) / twoSigmaSq);
                vec2 offset = (u_dir * i) / u_resolution;
                sum += texture(u_texture, fragTexCoord + offset) * weight;
                totalWeight += weight;
            }
            fragColor = sum / totalWeight;
        }
    """.trimIndent()

    /**
     * Compiles shaders and generates the core profile full-screen quad geometry.
     */
    fun init() {
        // Compile programs
        vertexShaderId = compileShader(GL_VERTEX_SHADER, vertexShaderSource)
        fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource)
        
        programId = glCreateProgram()
        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragmentShaderId)
        
        glBindAttribLocation(programId, 0, "position")
        glBindAttribLocation(programId, 1, "uv")
        
        glLinkProgram(programId)
        val linked = glGetProgrami(programId, GL_LINK_STATUS)
        if (linked == GL_FALSE) {
            throw IllegalStateException("Shader linking failed: " + glGetProgramInfoLog(programId))
        }

        setupQuadGeometry()
    }

    /**
     * Instantiates the vertex and texture coordinate attributes on a GPU-buffered quad.
     */
    private fun setupQuadGeometry() {
        quadVao = glGenVertexArrays()
        quadVbo = glGenBuffers()

        // 4 vertices (X, Y, U, V) making up a full-screen triangle fan
        val data = floatArrayOf(
            -1.0f, -1.0f,  0.0f, 0.0f,
             1.0f, -1.0f,  1.0f, 0.0f,
             1.0f,  1.0f,  1.0f, 1.0f,
            -1.0f,  1.0f,  0.0f, 1.0f
        )

        val buffer = MemoryUtil.memAllocFloat(data.size)
        buffer.put(data)
        buffer.flip()

        glBindVertexArray(quadVao)
        glBindBuffer(GL_ARRAY_BUFFER, quadVbo)
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)

        // Bind layouts (0 -> Position vector, 1 -> Texture Coordinate)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0L)
        glEnableVertexAttribArray(0)
        
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8L)
        glEnableVertexAttribArray(1)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        MemoryUtil.memFree(buffer)
    }

    /**
     * Executes two-pass post-processing blur.
     */
    fun renderBlur(radius: Float) {
        if (radius < 0.1f) return

        val mc = Minecraft.getInstance()
        val width = mc.window.width
        val height = mc.window.height

        checkFboSize(width, height)

        val originalFbo = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING)

        glUseProgram(programId)

        val radiusLoc = glGetUniformLocation(programId, "u_radius")
        val dirLoc = glGetUniformLocation(programId, "u_dir")
        val resLoc = glGetUniformLocation(programId, "u_resolution")
        
        glUniform1f(radiusLoc, radius)
        glUniform2f(resLoc, width.toFloat(), height.toFloat())

        // Pass 1: Horizontal pass (source: Minecraft viewport texture -> Ping-Pong frame buffer texture)
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)
        glClear(GL_COLOR_BUFFER_BIT)
        glUniform2f(dirLoc, 1.0f, 0.0f)
        
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, mc.mainRenderTarget.colorTextureId)
        
        drawFullscreenQuad()

        // Pass 2: Vertical pass (source: Ping-Pong frame buffer texture -> default screen frame buffer)
        glBindFramebuffer(GL_FRAMEBUFFER, originalFbo)
        glUniform2f(dirLoc, 0.0f, 1.0f)
        
        glBindTexture(GL_TEXTURE_2D, fboTextureId)
        
        drawFullscreenQuad()

        glUseProgram(0)
    }

    private fun compileShader(type: Int, source: String): Int {
        val id = glCreateShader(type)
        glShaderSource(id, source)
        glCompileShader(id)
        val compiled = glGetShaderi(id, GL_COMPILE_STATUS)
        if (compiled == GL_FALSE) {
            throw IllegalStateException("Shader compiling failed: " + glGetShaderInfoLog(id))
        }
        return id
    }

    private fun checkFboSize(w: Int, h: Int) {
        if (w == lastWidth && h == lastHeight && fboId != 0) return
        
        lastWidth = w
        lastHeight = h

        if (fboId != 0) {
            glDeleteFramebuffers(fboId)
            glDeleteTextures(fboTextureId)
        }

        fboId = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)

        fboTextureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, fboTextureId)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, null as java.nio.ByteBuffer?)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTextureId, 0)

        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw IllegalStateException("Framebuffer setup failed: $status")
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    private fun drawFullscreenQuad() {
        glBindVertexArray(quadVao)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)
        glBindVertexArray(0)
    }

    /**
     * Cleans up hardware buffers, arrays, programs and intermediate textures.
     */
    fun cleanup() {
        if (programId != 0) {
            glDeleteProgram(programId)
            glDeleteShader(vertexShaderId)
            glDeleteShader(fragmentShaderId)
        }
        if (quadVao != 0) {
            glDeleteVertexArrays(quadVao)
        }
        if (quadVbo != 0) {
            glDeleteBuffers(quadVbo)
        }
        if (fboId != 0) {
            glDeleteFramebuffers(fboId)
            glDeleteTextures(fboTextureId)
        }
    }
}
