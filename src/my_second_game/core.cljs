(ns my-second-game.core)

(defn ^:export main []
  (let [app (js/PIXI.Application. #js {:backgroundColor 0x1099bb})
        basic-text (js/PIXI.Text. "Hello Pixi!")]
    (.addChild (.-stage app) basic-text)
    (.append (.-body js/document) (.-view app))))
