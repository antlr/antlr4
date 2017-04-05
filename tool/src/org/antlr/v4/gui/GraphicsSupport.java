/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.gui;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class GraphicsSupport {
	/**
	 [The "BSD license"]
	 Copyright (c) 2011 Cay Horstmann
	 All rights reserved.

	 Redistribution and use in source and binary forms, with or without
	 modification, are permitted provided that the following conditions
	 are met:

	 1. Redistributions of source code must retain the above copyright
	 notice, this list of conditions and the following disclaimer.
	 2. Redistributions in binary form must reproduce the above copyright
	 notice, this list of conditions and the following disclaimer in the
	 documentation and/or other materials provided with the distribution.
	 3. The name of the author may not be used to endorse or promote products
	 derived from this software without specific prior written permission.

	 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
	 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
	 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
	 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
	 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
	 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
	 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
	 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
	 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */
	public static void saveImage(final JComponent comp, String fileName)
		throws IOException, PrintException
	{
		if ( fileName.endsWith(".ps") || fileName.endsWith(".eps") ) {
			DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
			String mimeType = "application/postscript";
			StreamPrintServiceFactory[] factories =
				StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavor, mimeType);
			System.out.println(Arrays.toString(factories));
			if (factories.length > 0) {
				FileOutputStream out = new FileOutputStream(fileName);
				PrintService service = factories[0].getPrintService(out);
				SimpleDoc doc = new SimpleDoc(new Printable() {
					@Override
					public int print(Graphics g, PageFormat pf, int page) {
						if (page >= 1) return Printable.NO_SUCH_PAGE;
						else {
							Graphics2D g2 = (Graphics2D) g;
							g2.translate((pf.getWidth() - pf.getImageableWidth()) / 2,
										 (pf.getHeight() - pf.getImageableHeight()) / 2);
							if ( comp.getWidth() > pf.getImageableWidth() ||
								 comp.getHeight() > pf.getImageableHeight() )
							{
								double sf1 = pf.getImageableWidth() / (comp.getWidth() + 1);
								double sf2 = pf.getImageableHeight() / (comp.getHeight() + 1);
								double s = Math.min(sf1, sf2);
								g2.scale(s, s);
							}

							comp.paint(g);
							return Printable.PAGE_EXISTS;
						}
					}
				}, flavor, null);
				DocPrintJob job = service.createPrintJob();
				PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
				job.print(doc, attributes);
				out.close();
			}
		}
		else {
			// parrt: works with [image/jpeg, image/png, image/x-png, image/vnd.wap.wbmp, image/bmp, image/gif]
			Rectangle rect = comp.getBounds();
			BufferedImage image = new BufferedImage(rect.width, rect.height,
													BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) image.getGraphics();
			g.setColor(Color.WHITE);
			g.fill(rect);
//			g.setColor(Color.BLACK);
			comp.paint(g);
			String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
			boolean result = ImageIO.write(image, extension, new File(fileName));
			if ( !result ) {
				System.err.println("Now imager for " + extension);
			}
			g.dispose();
		}
	}
}
