(ns common.const)

(def project-name "Soundersteel")

(def project-extension "ssp")

(def electron-ipc-channels {:bidirectional ["join"
                                            "dirname"
                                            "basename"
                                            "relative"
                                            "absolute?"
                                            "file-exist?"
                                            "create-directory"
                                            "read-file"
                                            "write-file"
                                            "open-file"
                                            "show-open-dialog"
                                            "show-save-dialog"]
                            :to-main       []
                            :from-main     ["open-project"
                                            "save-project-as"

                                            ;; For global shortcuts
                                            "move-to-next-scene"
                                            "move-to-prev-scene"
                                            "toggle-playing-state"
                                            "toggle-loop?"
                                            "increase-master-volume"]})
