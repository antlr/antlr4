#include "SystemFontMetrics.h"

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {
                    namespace gui {

                        SystemFontMetrics::SystemFontMetrics(const std::wstring &fontName) : font(new Font(fontName, Font::PLAIN, 1000)) {
                            BufferedImage *img = new BufferedImage(40, 40, BufferedImage::TYPE_4BYTE_ABGR);
                            Graphics2D *graphics = GraphicsEnvironment::getLocalGraphicsEnvironment()->createGraphics(img);
                            FontRenderContext *fontRenderContext = graphics->getFontRenderContext();
                            double maxHeight = 0;
                            for (int i = 0; i < 255; i++) {
//JAVA TO C++ CONVERTER TODO TASK: There is no native C++ equivalent to 'toString':
                                TextLayout *layout = new TextLayout(wchar_t::toString(static_cast<wchar_t>(i)), font, fontRenderContext);
                                maxHeight = std::max(maxHeight, layout->getBounds()->getHeight());
                                BasicFontMetrics::widths[i] = static_cast<int>(layout->getAdvance());
                            }

                            BasicFontMetrics::maxCharHeight = static_cast<int>(Math::round(maxHeight));
                        }

                        Font *SystemFontMetrics::getFont() {
                            return font;
                        }
                    }
                }
            }
        }
    }
}
