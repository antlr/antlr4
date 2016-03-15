#include "GraphicsSupport.h"

/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// TODO:  Come back to this after the base runtime works.
#if 0

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace misc {

                    void GraphicsSupport::saveImage(JComponent *const comp, const std::wstring &fileName) throw(IOException, PrintException) {
//JAVA TO C++ CONVERTER TODO TASK: There is no direct native C++ equivalent to the Java String 'endsWith' method:
                        if (fileName.endsWith(L".ps") || fileName.endsWith(L".eps")) {
                            DocFlavor *flavor = DocFlavor::SERVICE_FORMATTED::PRINTABLE;
                            std::wstring mimeType = L"application/postscript";
//JAVA TO C++ CONVERTER WARNING: Since the array size is not known in this declaration, Java to C++ Converter has converted this array to a pointer.  You will need to call 'delete[]' where appropriate:
//ORIGINAL LINE: javax.print.StreamPrintServiceFactory[] factories = javax.print.StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavor, mimeType);
                            StreamPrintServiceFactory *factories = StreamPrintServiceFactory::lookupStreamPrintServiceFactories(flavor, mimeType);
//JAVA TO C++ CONVERTER TODO TASK: There is no native C++ equivalent to 'toString':
                            std::cout << Arrays->toString(factories) << std::endl;
                            if (factories->length > 0) {
                                FileOutputStream *out = new FileOutputStream(fileName);
                                PrintService *service = factories[0]->getPrintService(out);
                                SimpleDoc *doc = new SimpleDoc(new PrintableAnonymousInnerClassHelper(comp), flavor, nullptr);
                                DocPrintJob *job = service->createPrintJob();
                                PrintRequestAttributeSet *attributes = new HashPrintRequestAttributeSet();
                                job->print(doc, attributes);
                                out->close();
                            }
                        } else {
                            // parrt: works with [image/jpeg, image/png, image/x-png, image/vnd.wap.wbmp, image/bmp, image/gif]
                            Rectangle *rect = comp->getBounds();
                            BufferedImage *image = new BufferedImage(rect->width, rect->height, BufferedImage::TYPE_INT_RGB);
                            Graphics2D *g = static_cast<Graphics2D*>(image->getGraphics());
                            g->setColor(Color::WHITE);
                            g->fill(rect);
                                        //			g.setColor(Color.BLACK);
                            comp->paint(g);
                            std::wstring extension = fileName.substr(fileName.rfind(L'.') + 1);
                            bool result = ImageIO::write(image, extension, new File(fileName));
                            if (!result) {
                                System::err::println(std::wstring(L"Now imager for ") + extension);
                            }
                            g->dispose();
                        }
                    }

                    GraphicsSupport::PrintableAnonymousInnerClassHelper::PrintableAnonymousInnerClassHelper(JComponent *comp) {
                        this->comp = comp;
                    }

                    int GraphicsSupport::PrintableAnonymousInnerClassHelper::print(Graphics *g, PageFormat *pf, int page) {
                        if (page >= 1) {
                            return Printable::NO_SUCH_PAGE;
                        } else {
                            Graphics2D *g2 = static_cast<Graphics2D*>(g);
                            g2->translate((pf->getWidth() - pf->getImageableWidth()) / 2, (pf->getHeight() - pf->getImageableHeight()) / 2);
                            if (comp->getWidth() > pf->getImageableWidth() || comp->getHeight() > pf->getImageableHeight()) {
                                double sf1 = pf->getImageableWidth() / (comp->getWidth() + 1);
                                double sf2 = pf->getImageableHeight() / (comp->getHeight() + 1);
                                double s = std::min(sf1, sf2);
                                g2->scale(s, s);
                            }

                            comp->paint(g);
                            return Printable::PAGE_EXISTS;
                        }
                    }
                }
            }
        }
    }
}
#endif
