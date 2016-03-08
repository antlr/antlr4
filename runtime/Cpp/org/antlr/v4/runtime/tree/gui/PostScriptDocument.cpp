#include "PostScriptDocument.h"

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {
                    namespace gui {

const std::wstring PostScriptDocument::DEFAULT_FONT = L"Courier New";

//JAVA TO C++ CONVERTER TODO TASK: Static constructors are not allowed in native C++:
                        PostScriptDocument::PostScriptDocument() {
                            POSTSCRIPT_FONT_NAMES = std::unordered_map<std::wstring, std::wstring>();
                            POSTSCRIPT_FONT_NAMES->put(Font::SANS_SERIF + std::wstring(L".plain"), L"ArialMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::SANS_SERIF + std::wstring(L".bold"), L"Arial-BoldMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::SANS_SERIF + std::wstring(L".italic"), L"Arial-ItalicMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::SANS_SERIF + std::wstring(L".bolditalic"), L"Arial-BoldItalicMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::SERIF + std::wstring(L".plain"), L"TimesNewRomanPSMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::SERIF + std::wstring(L".bold"), L"TimesNewRomanPS-BoldMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::SERIF + std::wstring(L".italic"), L"TimesNewRomanPS-ItalicMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::SERIF + std::wstring(L".bolditalic"), L"TimesNewRomanPS-BoldItalicMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::MONOSPACED + std::wstring(L".plain"), L"CourierNewPSMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::MONOSPACED + std::wstring(L".bold"), L"CourierNewPS-BoldMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::MONOSPACED + std::wstring(L".italic"), L"CourierNewPS-ItalicMT");
                            POSTSCRIPT_FONT_NAMES->put(Font::MONOSPACED + std::wstring(L".bolditalic"), L"CourierNewPS-BoldItalicMT");
                        }

//JAVA TO C++ CONVERTER TODO TASK: Calls to same-class constructors are not supported in C++ prior to C++11:
                        PostScriptDocument::PostScriptDocument() {
                        }

                        PostScriptDocument::PostScriptDocument(const std::wstring &fontName, int fontSize) {
                            InitializeInstanceFields();
                            header();
                            setFont(fontName, fontSize);
                        }

                        std::wstring PostScriptDocument::getPS() {
                            close();
//JAVA TO C++ CONVERTER TODO TASK: There is no native C++ equivalent to 'toString':
                            return header() + ps->toString();
                        }

                        void PostScriptDocument::boundingBox(int w, int h) {
                            boundingBoxWidth = w;
                            boundingBoxHeight = h;
                            boundingBox_Renamed = std::wstring::format(Locale::US, L"%%%%BoundingBox: %d %d %d %d\n", 0,0, boundingBoxWidth,boundingBoxHeight);
                        }

                        void PostScriptDocument::close() {
                            if (closed) {
                                return;
                            }
                                                //		ps.append("showpage\n");
                            ps->append(L"%%Trailer\n");
                            closed = true;
                        }

                        StringBuilder *PostScriptDocument::header() {
                            StringBuilder *b = new StringBuilder();
                            b->append(L"%!PS-Adobe-3.0 EPSF-3.0\n");
                            b->append(boundingBox_Renamed)->append(L"\n");
                            b->append(L"0.3 setlinewidth\n");
                            b->append(std::wstring(L"%% x y w h highlight\n") + std::wstring(L"/highlight {\n") + std::wstring(L"        4 dict begin\n") + std::wstring(L"        /h exch def\n") + std::wstring(L"        /w exch def\n") + std::wstring(L"        /y exch def\n") + std::wstring(L"        /x exch def\n") + std::wstring(L"        gsave\n") + std::wstring(L"        newpath\n") + std::wstring(L"        x y moveto\n") + std::wstring(L"        0 h rlineto     % up to left corner\n") + std::wstring(L"        w 0 rlineto     % to upper right corner\n") + std::wstring(L"        0 h neg rlineto % to lower right corner\n") + std::wstring(L"        w neg 0 rlineto % back home to lower left corner\n") + std::wstring(L"        closepath\n") + std::wstring(L"        .95 .83 .82 setrgbcolor\n") + std::wstring(L"        fill\n") + std::wstring(L"        grestore\n") + std::wstring(L"        end\n") + std::wstring(L"} def\n"));

                            return b;
                        }

                        void PostScriptDocument::setFont(const std::wstring &fontName, int fontSize) {
                            this->fontMetrics = new SystemFontMetrics(fontName);
                            this->fontName = fontMetrics->getFont()->getPSName();
                            this->fontSize = fontSize;

                            std::wstring psname = POSTSCRIPT_FONT_NAMES->get(this->fontName);
                            if (psname == L"") {
                                psname = this->fontName;
                            }

                            ps->append(std::wstring::format(Locale::US, L"/%s findfont %d scalefont setfont\n", psname, fontSize));
                        }

                        void PostScriptDocument::lineWidth(double w) {
                            lineWidth_Renamed = w;
                            ps->append(w)->append(L" setlinewidth\n");
                        }

                        void PostScriptDocument::move(double x, double y) {
                            ps->append(std::wstring::format(Locale::US, L"%1.3f %1.3f moveto\n", x, y));
                        }

                        void PostScriptDocument::lineto(double x, double y) {
                            ps->append(std::wstring::format(Locale::US, L"%1.3f %1.3f lineto\n", x, y));
                        }

                        void PostScriptDocument::line(double x1, double y1, double x2, double y2) {
                            move(x1, y1);
                            lineto(x2, y2);
                        }

                        void PostScriptDocument::rect(double x, double y, double width, double height) {
                            line(x, y, x, y + height);
                            line(x, y + height, x + width, y + height);
                            line(x + width, y + height, x + width, y);
                            line(x + width, y, x, y);
                        }

                        void PostScriptDocument::highlight(double x, double y, double width, double height) {
                            ps->append(std::wstring::format(Locale::US, L"%1.3f %1.3f %1.3f %1.3f highlight\n", x, y, width, height));
                        }

                        void PostScriptDocument::stroke() {
                            ps->append(L"stroke\n");
                        }

                        void PostScriptDocument::text(const std::wstring &s, double x, double y) {
                            StringBuilder *buf = new StringBuilder();
                            // escape \, (, ): \\,  \(,  \)
                            for (auto c : s.toCharArray()) {
                                switch (c) {
                                    case L'\\' :
                                    case L'(' :
                                    case L')' :
                                        buf->append(L'\\');
                                        buf->append(c);
                                        break;
                                    default :
                                        buf->append(c);
                                        break;
                                }
                            }
//JAVA TO C++ CONVERTER TODO TASK: There is no native C++ equivalent to 'toString':
                            s = buf->toString();
                            move(x,y);
                            ps->append(std::wstring::format(Locale::US, L"(%s) show\n", s));
                            stroke();
                        }

                        double PostScriptDocument::getWidth(wchar_t c) {
                            return fontMetrics->getWidth(c, fontSize);
                        }

                        double PostScriptDocument::getWidth(const std::wstring &s) {
                            return fontMetrics->getWidth(s, fontSize);
                        }

                        double PostScriptDocument::getLineHeight() {
                            return fontMetrics->getLineHeight(fontSize);
                        }

                        int PostScriptDocument::getFontSize() {
                            return fontSize;
                        }

                        void PostScriptDocument::InitializeInstanceFields() {
                            boundingBoxWidth = 0;
                            boundingBoxHeight = 0;
                            fontSize = 12;
                            lineWidth_Renamed = 0.3;
                            ps = new StringBuilder();
                            closed = false;
                        }
                    }
                }
            }
        }
    }
}
