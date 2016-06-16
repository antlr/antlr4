/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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

#include "RuntimeMetaData.h"

using namespace antlr4;

const std::string RuntimeMetaData::VERSION = "4.5.4";

std::string RuntimeMetaData::getRuntimeVersion() {
  return VERSION;
}

void RuntimeMetaData::checkVersion(const std::string &generatingToolVersion, const std::string &compileTimeVersion) {
  std::string runtimeVersion = VERSION;
  bool runtimeConflictsWithGeneratingTool = false;
  bool runtimeConflictsWithCompileTimeTool = false;

  if (generatingToolVersion != "") {
    runtimeConflictsWithGeneratingTool = runtimeVersion != generatingToolVersion
      && getMajorMinorVersion(runtimeVersion) != getMajorMinorVersion(generatingToolVersion);
  }

  runtimeConflictsWithCompileTimeTool = runtimeVersion != compileTimeVersion
    && getMajorMinorVersion(runtimeVersion) != getMajorMinorVersion(compileTimeVersion);

  if (runtimeConflictsWithGeneratingTool) {
    std::cerr << "ANTLR Tool version " << generatingToolVersion << " used for code generation does not match "
      "the current runtime version " << runtimeVersion << std::endl;
  }
  if (runtimeConflictsWithCompileTimeTool) {
    std::cerr << "ANTLR Runtime version " << compileTimeVersion << " used for parser compilation does not match "
      "the current runtime version " << runtimeVersion << std::endl;
  }
}

std::string RuntimeMetaData::getMajorMinorVersion(const std::string &version) {
  size_t firstDot = version.find('.');
  size_t secondDot = firstDot != std::string::npos ? version.find('.', firstDot + 1) : std::string::npos;
  size_t firstDash = version.find('-');
  size_t referenceLength = version.size();
  if (secondDot != std::string::npos) {
    referenceLength = std::min(referenceLength, secondDot);
  }

  if (firstDash != std::string::npos) {
    referenceLength = std::min(referenceLength, firstDash);
  }

  return version.substr(0, referenceLength);
}
