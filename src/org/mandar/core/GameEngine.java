package org.mandar.core;

import org.mandar.ImGuiLayer;
import org.mandar.debug.Debug;
import org.mandar.event.Event;
import org.mandar.event.EventDispatcher;
import org.mandar.event.EventType;
import org.mandar.event.IEventListener;
import org.mandar.renderer.RenderingAPI;

import java.util.Arrays;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class GameEngine implements Runnable, IEventListener {
    public static GameEngine engine; //static ref to the engine

    private final Window window;

    private LinkedList<Layer> layers;

    private ImGuiLayer imGuiLayer;

    private boolean isRunning;

    private float targetFPS;
    private float updatesPerSec;

    public GameEngine(String windowTitle, RenderingAPI api, Layer... gameLogic) throws Exception{
        this(windowTitle, 800, 600, 60, 60, false, api, gameLogic);
    }

    public GameEngine(String windowTitle, int windowWidth, int windowHeight, float maxFPS, float maxUpdates, boolean vSync, RenderingAPI api, Layer... layers) throws Exception{

        engine = this;

        window = Window.createWindow(api, windowTitle, windowWidth, windowHeight, vSync, true);

        this.layers = new LinkedList<>();
        this.layers.addAll(Arrays.asList(layers));

        imGuiLayer = new ImGuiLayer();
        this.layers.add(imGuiLayer);

        this.targetFPS = maxFPS;
        this.updatesPerSec = maxUpdates;

        this.isRunning = true;

    }

    @Override
    public void run() {
        try{
            init();
            gameLoop();
            shutDown();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init() throws Exception{
        Debug.init();
        Time.init();

        window.init(this);

        for (Layer layer : this.layers) {
            layer.onAttach();
        }

    }

    private void gameLoop(){
        float updateTimer = 0f;

        while(isRunning){
            Time.update();
            updateTimer += Time.getDeltaTime();

            update();

            window.update();

            /*
            if(true && updateTimer >= 1/updatesPerSec){
                Debug.coreLog(updateTimer);
                updateTimer = 0f;
            }*/


            /*if(!window.isvSync()){
                customSync();
            }*/
        }
    }

    private void update(){
        for(Layer layer : layers)
            layer.update(Time.getDeltaTime());

        imGuiLayer.begin();
        for(Layer layer : layers)
            layer.onImGuiRender();
        imGuiLayer.end();
    }

    //ends gameLoop
    public void close()
    {
        this.isRunning = false;
    }

    //clean up before closing
    private void shutDown() {
        for(Layer layer : layers) //shutdown all layers? May be needed
            layer.onDetach();
        this.window.close();
    }

    /////*EVENTS*/////
    @Override
    public void onEvent(Event e) {

        EventDispatcher dispatcher = new EventDispatcher(e);
        dispatcher.dispatch(EventType.WindowClose, this::onWindowClosed);
        dispatcher.dispatch(EventType.WindowResize, this::onWindowResized);

        //if event wasn't handled yet, then propagate it through the layers
        for(Layer l : layers)
        {
            if(e.handled) break;
            l.onEvent(e);
        }
    }

    private boolean onWindowClosed(Event.WindowCloseEvent e) {
        this.isRunning = false;
        return true;
    }
    private boolean onWindowResized(Event.WindowResizedEvent e) { return true; }

    private void customSync() {
        float fpsTime = 1/targetFPS;
        double syncTime = Time.getSystemTime() + fpsTime;
        while(Time.getSystemTime() <= syncTime){
            try{
                Thread.sleep(1);
            } catch(Exception e){
                System.err.println("Custom Sync Sleep Error");
            }
        }
    }

    public Window getWindow() { return this.window; }
}
