(ns br.eng.crisjr.tools-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [br.eng.crisjr.failproof.tools :as tools]))

(defn not-nil? [etwas]
    (is (not (nil? etwas))))

;; CLOJURE TESTS
(deftest load-lists
    (testing "can it load the lists in the correct format?"
        (not-nil? (let [stuff (tools/get-lists)]
            (do (println (reduce #(str %1 " " %2) stuff))
                stuff)))))

(deftest load-list
    (testing "can it load a list?"
        (not-nil? (let [list (tools/get-list "apps.yml")]
            (do (println list)
                list)))))

(deftest lists-to-titles
    (testing "can I turn the id list into a list of titles?"
        (not-nil? (let [stuff (tools/get-lists)
                        titles (tools/to-titles stuff)]
            (do (println "titles:")
                (println (reduce #(str %1 "-" %2 "\n") "" titles))
                titles)))))

(deftest lists-to-links
    (testing "can I turn the id list into a list of links?"
        (not-nil? (let [stuff (tools/get-lists)
                        links (tools/to-links stuff)]
            (do (println "links:")
                (println (reduce #(str %1 "-" %2 "\n") "" links))
                links)))))

(deftest title-from-list
    (testing "can I get the title of checklist in API format?"
        (is (= "Everyday Carry" (tools/get-title (tools/get-list "edc.yml"))))))

(deftest items-from-list
    (testing "can I get the items from a checklist?"
        (is (loop [n 0
                   outlet true
                   items (tools/get-items (tools/get-list "functionalworkout.yml"))
                   answers '("Jumping jacks"
                             "Wall sit"
                             "Push ups"
                             "Abdominal crunch"
                             "Step up onto chair"
                             "Squat"
                             "Triceps dip on chair"
                             "Plank"
                             "High knees running in place"
                             "Lunge"
                             "Push up and rotation"
                             "Side plank")
                   limit (count answers)]
            (if (= n limit)
                outlet
                (recur (+ n 1)
                       (and outlet (= (nth items n)
                                      (nth answers n)))
                       items
                       answers
                       limit))))))
