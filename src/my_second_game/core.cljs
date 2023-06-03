(ns my-second-game.core
  (:require [cljs.pprint :refer [pprint]]))

(def speed 5)
(def fox-speed 4)
(def state (atom {:bunny {:x 100 :y 100 :vx 0 :vy 0}
                  :fox {:x 0 :y 0 :vx 0 :vy 0}}))

(comment
  (pprint @state)
  (cond-> {} :always (assoc :a 1) true (assoc :b 2)))

(defn update-state []
  (swap! state
         (fn [state]
           (let [{:keys [x y vx vy]} (:bunny state)
                 new-x               (+ x vx)
                 new-y               (+ y vy)
                 {fox-y :y fox-x :x} (:fox state)
                 dx                  (- x fox-x)
                 dy                  (- y fox-y)
                 distance            (Math/sqrt (+ (Math/pow dx 2) (Math/pow dy 2)))
                 normalized-dx       (/ dx distance)
                 normalized-dy       (/ dy distance)]

             (cond-> state
               :always
               (assoc-in [:bunny] {:x new-x :y new-y :vx 0 :vy 0})

               (and
                (not (zero? distance))
                (or (not (zero? vx))
                    (not (zero? vy))))
               (assoc-in [:fox] {:x (+ fox-x (* normalized-dx fox-speed))
                                 :y (+ fox-y (* normalized-dy fox-speed))}))))))

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
        fox-texture (js/PIXI.Texture.from "textures/fox.png")
        bunny (js/PIXI.Sprite. bunny-texture)
        fox (js/PIXI.Sprite. fox-texture)]

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
