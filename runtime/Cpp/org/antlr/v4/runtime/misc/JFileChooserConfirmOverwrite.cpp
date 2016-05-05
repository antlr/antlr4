#include "JFileChooserConfirmOverwrite.h"
#ifdef TODO
namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace misc {

                    JFileChooserConfirmOverwrite::JFileChooserConfirmOverwrite() {
                        setMultiSelectionEnabled(false);
                    }

                    void JFileChooserConfirmOverwrite::approveSelection() {
                        File *selectedFile = getSelectedFile();

                        if (selectedFile->exists()) {
                            int answer = JOptionPane::showConfirmDialog(this, L"Overwrite existing file?", L"Overwrite?", JOptionPane::YES_NO_OPTION);
                            if (answer != JOptionPane::YES_OPTION) {
                                // do not call super.approveSelection
                                return;
                            }
                        }

                        JFileChooser::approveSelection();
                    }
                }
            }
        }
    }
}

#endif
