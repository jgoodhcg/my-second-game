(ns my-second-game.core)

(defn ^:export main []
  (let [app (js/PIXI.Application. #js {:backgroundColor 0x1099bb})
        texture (js/PIXI.Texture.from "https://pixijs.io/examples/examples/assets/bunny.png")
        bunny (js/PIXI.Sprite. texture)
        speed 5] ;; Set the speed of character's movement

    (set! (.-x bunny) 0)
    (set! (.-y bunny) 0)

    ;; Adding the sprite to the stage
    (.addChild (.-stage app) bunny)

    ;; Adding the application view to the document body
    (.append (.-body js/document) (.-view app))

    ;; Adding keyboard event listeners
    (js/window.addEventListener "keydown" (fn [event]
                                              (let [code (.-code event)]
                                                (condp = code
                                                  "ArrowUp"    (set! (.-y bunny) (- (.-y bunny) speed))
                                                  "ArrowDown"  (set! (.-y bunny) (+ (.-y bunny) speed))
                                                  "ArrowLeft"  (set! (.-x bunny) (- (.-x bunny) speed))
                                                  "ArrowRight" (set! (.-x bunny) (+ (.-x bunny) speed))))))
))
