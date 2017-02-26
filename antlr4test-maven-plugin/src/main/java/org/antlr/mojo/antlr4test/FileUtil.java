/*
 [The "BSD license"]
 Copyright (c) 2014 Tom Everett
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
package org.antlr.mojo.antlr4test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Everett
 */
public class FileUtil {
   /**
    * find files
    */
   public static List<File> getAllFiles(String dir) throws Exception {
      return getAllFiles(dir, null);
   }

   /**
    * find files
    */
   public static List<File> getAllFiles(String dir, String extension) {
      final List<File> ret = new ArrayList<File>();
      final File file = new File(dir);
      if (file.exists()) {
         final String[] list = file.list();
         for (int i = 0; i < list.length; i++) {
            final String fileName = dir + "/" + list[i];
            final File f2 = new File(fileName);
            if (false == f2.isHidden()) {
               if (f2.isDirectory()) {
                  ret.addAll(getAllFiles(fileName, extension));
               } else {
                  if (null != extension) {
                     if (f2.getName().endsWith(extension)) {
                        ret.add(f2);
                     }
                  } else {
                     ret.add(f2);
                  }
               }
            }
         }
         return ret;
      } else {
         return null;
      }
   }
}
