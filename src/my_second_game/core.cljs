(ns my-second-game.core)

(def speed 5)
(def fox-speed 20) ;; Assuming fox is slower than the bunny
(def state (atom {:bunny {:x 100 :y 100 :vx 0 :vy 0}
                  :fox {:x 0 :y 0 :vx 0 :vy 0}})) ;; Add the fox to the state

(defn update-state []
  (swap! state
         (fn [state]
           (let [{:keys [x y vx vy]} (:bunny state)
                 new-x (+ x vx)
                 new-y (+ y vy)
                 {:keys [x fox-x y fox-y]} (:fox state)
                 fox-dir (Math/atan2 (- new-y fox-y) (- new-x fox-x))]
             (-> state
                 (assoc-in [:bunny :x] new-x)
                 (assoc-in [:bunny :y] new-y)
                 (assoc-in [:bunny :vx] 0)
                 (assoc-in [:bunny :vy] 0)
                 (assoc-in [:fox :x] (+ fox-x (* (Math/cos fox-dir) fox-speed)))
                 (assoc-in [:fox :y] (+ fox-y (* (Math/sin fox-dir) fox-speed))))))))

(defn render [app bunny fox]
  (let [{:keys [x y]} (:bunny @state)]
    (set! (.-x bunny) x)
    (set! (.-y bunny) y))
  (let [{:keys [x y]} (:fox @state)]
    (set! (.-x fox) x)
    (set! (.-y fox) y)))

(defn game-loop [app bunny fox]
  (fn []
    (update-state)
    (render app bunny fox)
    (js/requestAnimationFrame (game-loop app bunny fox))))

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
  (let [app (js/PIXI.Application. #js {:backgroundColor 0xffffff})
        bunny-texture (js/PIXI.Texture.from "textures/bunny.png")
        fox-texture (js/PIXI.Texture.from "textures/fox.png") ;; Update this path
        bunny (js/PIXI.Sprite. bunny-texture)
        fox (js/PIXI.Sprite. fox-texture)] ;; Create the fox sprite

    (set! (.-height fox) 128)
    (set! (.-width fox) 128)
    (set! (.-height bunny) 128)
    (set! (.-width bunny) 128)

    ;; Adding the sprites to the stage
    (.addChild (.-stage app) bunny)
    (.addChild (.-stage app) fox) ;; Add the fox to the stage

    ;; Adding the application view to the document body
    (.append (.-body js/document) (.-view app))

    ;; Adding keyboard event listeners
    (js/window.addEventListener "keydown" handle-keydown)

    ;; Start game loop
    ((game-loop app bunny fox)))) ;; Pass the fox to game-loop
