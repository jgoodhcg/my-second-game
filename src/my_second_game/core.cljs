(ns my-second-game.core)

(def speed 5)
(def state (atom {:bunny {:x 0 :y 0 :vx 0 :vy 0}}))

(defn update-state []
  (swap! state update :bunny (fn [bunny]
                               (let [{:keys [x y vx vy]} bunny
                                     new-x (+ x vx)
                                     new-y (+ y vy)]
                                 (assoc bunny :x new-x :y new-y :vx 0 :vy 0)))))

(defn render [app bunny]
  (let [{:keys [x y]} (:bunny @state)]
    (set! (.-x bunny) x)
    (set! (.-y bunny) y)))

(defn game-loop [app bunny]
  (fn []
    (update-state)
    (render app bunny)
    (js/requestAnimationFrame (game-loop app bunny))))

(defn handle-keydown [event]
  (let [code (.-code event)]
    (swap! state update :bunny
           (fn [bunny]
             (condp = code
               "ArrowUp"    (assoc bunny :vy (- speed))
               "ArrowDown"  (assoc bunny :vy speed)
               "ArrowLeft"  (assoc bunny :vx (- speed))
               "ArrowRight" (assoc bunny :vx speed)
               bunny)))))

(defn ^:export main []
  (let [app (js/PIXI.Application. #js {:backgroundColor 0x1099bb})
        texture (js/PIXI.Texture.from "https://pixijs.io/examples/examples/assets/bunny.png")
        bunny (js/PIXI.Sprite. texture)]

    ;; Adding the sprite to the stage
    (.addChild (.-stage app) bunny)

    ;; Adding the application view to the document body
    (.append (.-body js/document) (.-view app))

    ;; Adding keyboard event listeners
    (js/window.addEventListener "keydown" handle-keydown)

    ;; Start game loop
    ((game-loop app bunny))))
