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

using namespace org::antlr::v4::runtime;

const std::wstring RuntimeMetaData::VERSION = L"4.5.3";

std::wstring RuntimeMetaData::getRuntimeVersion() {
  return VERSION;
}

void RuntimeMetaData::checkVersion(const std::wstring &generatingToolVersion, const std::wstring &compileTimeVersion) {
  std::wstring runtimeVersion = VERSION;
  bool runtimeConflictsWithGeneratingTool = false;
  bool runtimeConflictsWithCompileTimeTool = false;

  if (generatingToolVersion != L"") {
    runtimeConflictsWithGeneratingTool = runtimeVersion != generatingToolVersion
      && getMajorMinorVersion(runtimeVersion) != getMajorMinorVersion(generatingToolVersion);
  }

  runtimeConflictsWithCompileTimeTool = runtimeVersion != compileTimeVersion
    && getMajorMinorVersion(runtimeVersion) != getMajorMinorVersion(compileTimeVersion);

  if (runtimeConflictsWithGeneratingTool) {
    std::wcerr << "ANTLR Tool version " << generatingToolVersion << " used for code generation does not match "
      "the current runtime version " << runtimeVersion << std::endl;
  }
  if (runtimeConflictsWithCompileTimeTool) {
    std::wcerr << "ANTLR Runtime version " << compileTimeVersion << " used for parser compilation does not match "
      "the current runtime version " << runtimeVersion << std::endl;
  }
}

std::wstring RuntimeMetaData::getMajorMinorVersion(const std::wstring &version) {
  size_t firstDot = version.find(L'.');
  size_t secondDot = firstDot != std::wstring::npos ? version.find(L'.', firstDot + 1) : std::wstring::npos;
  size_t firstDash = version.find(L'-');
  size_t referenceLength = version.size();
  if (secondDot != std::wstring::npos) {
    referenceLength = std::min(referenceLength, secondDot);
  }

  if (firstDash != std::wstring::npos) {
    referenceLength = std::min(referenceLength, firstDash);
  }

  return version.substr(0, referenceLength);
}
