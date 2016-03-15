#include "BasicFontMetrics.h"

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {
                    namespace gui {

                        double BasicFontMetrics::getWidth(const std::wstring &s, int fontSize) {
                            double w = 0;
                            for (auto c : s.toCharArray()) {
                                w += getWidth(c, fontSize);
                            }
                            return w;
                        }

                        double BasicFontMetrics::getWidth(wchar_t c, int fontSize) {
                            if (c > MAX_CHAR || widths[c] == 0) { // return width('m')
                                return widths[L'm'] / 1000.0;
                            }
                            return widths[c] / 1000.0 * fontSize;
                        }

                        double BasicFontMetrics::getLineHeight(int fontSize) {
                            return maxCharHeight / 1000.0 * fontSize;
                        }

                        void BasicFontMetrics::InitializeInstanceFields() {
                            maxCharHeight = 0;
                        }
                    }
                }
            }
        }
    }
}
