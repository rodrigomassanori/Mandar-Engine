package org.mandar;

import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import org.mandar.core.GameEngine;
import org.mandar.core.Input;
import org.mandar.core.KeyCode;
import org.mandar.core.Layer;
import org.mandar.debug.Debug;
import org.mandar.event.Event;

public class EditorLayer extends Layer {

    ImGuiImplGlfw impl;
    ImGuiImplGl3 gl;

    @Override
    public void onAttach() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);


        GameEngine engine = GameEngine.engine;
        var window = engine.getWindow().getSystemWindow();


        impl = new ImGuiImplGlfw();
        impl.init(window, true);

        gl = new ImGuiImplGl3();
        gl.init("#version 410");


    }

    @Override
    public void update(float deltaTime) {
        if(Input.isKeyPressed(KeyCode.L)) {
            GameEngine.engine.getWindow().setDebugMode(false);
            Debug.log("L");
        }
    }

    public void onImGuiRender()
    {
        ImGui.showDemoWindow();
        ImGui.begin("hello");
        ImGui.text("a");
        ImGui.sameLine();
        ImGui.text("b");
        ImGui.end();
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onEvent(Event e) {

    }
}
