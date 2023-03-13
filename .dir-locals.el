;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; .dir-locals.el
;;
;; Configure CIDER to use the `:test' alias
;; as the default for the Clojure CLI tools project
;;
;; Injects the `-A:test' alias in the command line
;; when starting CIDER with `cider-jack-in'
;;
;; The `:test' alias loads the `clojure.test' library
;; as a project dependencies, enabling CIDER to run
;; tests from Emacs commands such as
;; `cider-test-run-project-tests'.
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

((clojure-mode . ((cider-clojure-cli-aliases . ":test"))))
