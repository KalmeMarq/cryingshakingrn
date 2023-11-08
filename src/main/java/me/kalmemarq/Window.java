package me.kalmemarq;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Window {
    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;
    private long handle;
    private String title;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private boolean isImGuiInitialized;
    private final List<WindowEventHandler> windowHandlers;
    private final List<KeyboardEventHandler> keyboardHandlers;
    private final List<MouseEventHandler> mouseHandlers;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.windowHandlers = new ArrayList<>();
        this.mouseHandlers = new ArrayList<>();
        this.keyboardHandlers = new ArrayList<>();
    }

    public void initialize() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW!");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0L, 0L);

        if (this.handle == 0L) {
            throw new RuntimeException("Could not create GLFW window!");
        }

        GLFW.glfwMakeContextCurrent(this.handle);

        var monitor = GLFW.glfwGetPrimaryMonitor();
        var videoMode = GLFW.glfwGetVideoMode(monitor);
        if (videoMode != null) {
            GLFW.glfwSetWindowPos(this.handle, (videoMode.width() - this.width) / 2, (videoMode.height() - this.height) / 2);
        }

        GLFW.glfwSwapInterval(1);
        GL.createCapabilities();

        GLFW.glfwShowWindow(this.handle);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pFW = stack.mallocInt(1);
            IntBuffer pFH = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(this.handle, pFW, pFH);
            this.framebufferWidth = pFW.get(0);
            this.framebufferHeight = pFH.get(0);
        }

        GL11.glViewport(0, 0, this.framebufferWidth, this.framebufferHeight);
        this.setupCallbacks();
    }

    private void setupCallbacks() {
        GLFW.glfwSetWindowSizeCallback(this.handle, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });
        GLFW.glfwSetFramebufferSizeCallback(this.handle, (window, width, height) -> {
            this.framebufferWidth = width;
            this.framebufferHeight = height;
            for (var handler : this.windowHandlers) {
                handler.onSizeChanged();
            }
        });
        GLFW.glfwSetMouseButtonCallback(this.handle, (window, button, action, mods) -> {
            for (var handler : this.mouseHandlers) {
                handler.onMouseButton(button, action, mods);
            }
        });
        GLFW.glfwSetCursorPosCallback(this.handle, (window, posX, posY) -> {
            for (var handler : this.mouseHandlers) {
                handler.onCursorPos(posX, posY);
            }
        });
        GLFW.glfwSetScrollCallback(this.handle, (window, offsetX, offsetY) -> {
            for (var handler : this.mouseHandlers) {
                handler.onScroll(offsetX, offsetY);
            }
        });
        GLFW.glfwSetDropCallback(this.handle, (window, count, names) -> {
            List<Path> paths = new ArrayList<>();
            for (int i = 0; i < count; ++i) {
                paths.add(Path.of(GLFWDropCallback.getName(names, i)));
            }
            for (var handler : this.mouseHandlers) {
                handler.onDrop(paths);
            }
        });
        GLFW.glfwSetKeyCallback(this.handle, (window, key, scancode, action, mods) -> {
            for (var handler : this.keyboardHandlers) {
                handler.onKey(key, scancode, action, mods);
            }
        });
        GLFW.glfwSetCharCallback(this.handle, (window, codepoint) -> {
            for (var handler : this.keyboardHandlers) {
                handler.onCharTyped(codepoint);
            }
        });
    }

    private void initializeImGui() {
        this.imGuiGlfw = new ImGuiImplGlfw();
        this.imGuiGl3 = new ImGuiImplGl3();
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        this.imGuiGlfw.init(this.handle, true);
        this.imGuiGl3.init("#version 150");
        System.out.println("ImGui initialized!");
    }

    public void beginImGuiFrame() {
        if (!this.isImGuiInitialized) {
            this.initializeImGui();
            this.isImGuiInitialized = true;
        }

        this.imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endImGuiFrame() {
        ImGui.render();
        this.imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void addWindowHandler(WindowEventHandler windowHandler) {
        this.windowHandlers.add(windowHandler);
    }

    public void addKeyboardHandler(KeyboardEventHandler keyboardHandler) {
        this.keyboardHandlers.add(keyboardHandler);
    }

    public void addMouseHandler(MouseEventHandler mouseHandler) {
        this.mouseHandlers.add(mouseHandler);
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(this.handle, title);
    }

    public void setVsync(boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public void update() {
        GLFW.glfwSwapBuffers(this.handle);
        GLFW.glfwPollEvents();
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    public void destroy() {
        if (this.isImGuiInitialized) {
            this.imGuiGl3.dispose();
            this.imGuiGlfw.dispose();
            ImGui.destroyContext();
        }

        Callbacks.glfwFreeCallbacks(this.handle);
        GLFW.glfwDestroyWindow(this.handle);
        GLFW.glfwTerminate();
    }

    public long getHandle() {
        return this.handle;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getContentWidth() {
        return this.framebufferWidth;
    }

    public int getContentHeight() {
        return this.framebufferHeight;
    }

    public interface WindowEventHandler {
        default void onSizeChanged() {
        }
    }

    public interface KeyboardEventHandler {
        default void onKey(int key, int scancode, int action, int modifiers) {
        }
        default void onCharTyped(int codepoint) {
        }
    }

    public interface MouseEventHandler {
        default void onMouseButton(int button, int action, int modifiers) {
        }
        default void onCursorPos(double posX, double posY) {
        }
        default void onScroll(double offsetX, double offsetY) {
        }
        default void onDrop(List<Path> paths) {
        }
    }
}