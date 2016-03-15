#! /usr/bin/perl -w

#
# colorgcc
#
# Version: 1.3.2
#
# $Id: colorgcc,v 1.10 1999/04/29 17:15:52 jamoyers Exp $
#
# A wrapper to colorize the output from compilers whose messages
# match the "gcc" format.
#
# Requires the ANSIColor module from CPAN.
#
# Usage:
#
# In a directory that occurs in your PATH _before_ the directory
# where the compiler lives, create a softlink to colorgcc for
# each compiler you want to colorize:
#
#    g++ -> colorgcc
#    gcc -> colorgcc
#    cc  -> colorgcc
#    etc.
#
# That's it. When "g++" is invoked, colorgcc is run instead.
# colorgcc looks at the program name to figure out which compiler to run.
#
# The default settings can be overridden with ~/.colorgccrc.
# See the comments in the sample .colorgccrc for more information.
#
# Note:
#
# colorgcc will only emit color codes if:
# 
#    (1) Its STDOUT is a tty and
#    (2) the value of $TERM is not listed in the "nocolor" option.
#
# If colorgcc colorizes the output, the compiler's STDERR will be
# combined with STDOUT. Otherwise, colorgcc just passes the output from
# the compiler through without modification.
# 
# Author: Jamie Moyers <jmoyers@geeks.com>
# Started: April 20, 1999
# Licence: GNU Public License
#
# Credits:
#
#    I got the idea for this from a script called "color_cvs":
#       color_cvs .03   Adrian Likins <adrian@gimp.org> <adrian@redhat.com>
#
#    <seh4@ix.netcom.com> (Scott Harrington)
#       Much improved handling of compiler command line arguments.
#       exec compiler when not colorizing to preserve STDOUT, STDERR.
#       Fixed my STDIN kludge.
#       
#    <ecarotti@athena.polito.it> (Elias S. G. Carotti)
#       Corrected handling of text like -DPACKAGE=\"Package\"
#       Spotted return code bug.
#
#    <erwin@erwin.andreasen.org> (Erwin S. Andreasen)
#    <schurchi@ucsd.edu> (Steve Churchill)
#       Return code bug fixes.
#
#    <rik@kde.org> (Rik Hemsley)
#       Found STDIN bug.
#
# Changes:
#
# 1.3.2 Better handling of command line arguments to compiler.
#
#       If we aren't colorizing output, we just exec the compiler which
#       preserves the original STDOUT and STDERR.
#
#       Removed STDIN kludge. STDIN being passed correctly now.
# 
# 1.3.1 Added kludge to copy STDIN to the compiler's STDIN.
#
# 1.3.0 Now correctly returns (I hope) the return code of the compiler
#       process as its own.
# 
# 1.2.1 Applied patch to handle text similar to -DPACKAGE=\"Package\".
#
# 1.2.0 Added tty check. If STDOUT is not a tty, don't do color.
#
# 1.1.0 Added the "nocolor" option to turn off the color if the terminal type
#       ($TERM) is listed.
#
# 1.0.0 Initial Version

use Term::ANSIColor;
use IPC::Open3;

sub initDefaults
{
   $compilerPaths{"gcc"} = "/usr/bin/gcc";
   $compilerPaths{"g++"} = "/usr/bin/g++";
   $compilerPaths{"cc"}  = "/usr/bin/cc";
   $compilerPaths{"c++"} = "/usr/bin/c++";

   $nocolor{"dumb"} = "true";

   $colors{"srcColor"} = color("cyan");
   $colors{"introColor"} = color("blue");

   $colors{"warningFileNameColor"} = color("yellow");
   $colors{"warningNumberColor"}   = color("yellow");
   $colors{"warningMessageColor"}  = color("yellow");

   $colors{"errorFileNameColor"} = color("bold red");
   $colors{"errorNumberColor"}   = color("bold red");
   $colors{"errorMessageColor"}  = color("bold red");
}

sub loadPreferences
{
# Usage: loadPreferences("filename");

   my($filename) = @_;

   open(PREFS, "<$filename") || return;

   while(<PREFS>)
   {
      next if (m/^\#.*/);          # It's a comment.
      next if (!m/(.*):\s*(.*)/);  # It's not of the form "foo: bar".

      $option = $1;
      $value = $2;

      if ($option =~ m/cc|c\+\+|gcc|g\+\+/)
      {
	 $compilerPaths{$option} = $value;
      }
      elsif ($option eq "nocolor")
      {
	 # The nocolor option lists terminal types, separated by
	 # spaces, not to do color on.
	 foreach $termtype (split(/\s+/, $value))
	 {
	    $nocolor{$termtype} = "true";
	 }
      }
      else
      {
	 $colors{$option} = color($value);
      }
   }
   close(PREFS);
}

sub srcscan
{
# Usage: srcscan($text, $normalColor)
#    $text -- the text to colorize
#    $normalColor -- The escape sequence to use for non-source text.

# Looks for text between ` and ', and colors it srcColor.

   my($line, $normalColor) = @_;

   my($srcon) = color("reset") . $colors{"srcColor"};
   my($srcoff) = color("reset") . $normalColor;

   $line = $normalColor . $line;

   # This substitute replaces `foo' with `AfooB' where A is the escape
   # sequence that turns on the the desired source color, and B is the
   # escape sequence that returns to $normalColor.
   $line =~ s/\`(.*?)\'/\`$srcon$1$srcoff\'/g;

   print($line, color("reset"));
}

#
# Main program
#

# Set up default values for colors and compilers.
initDefaults();

# Read the configuration file, if there is one.
$configFile = $ENV{"HOME"} . "/.colorgccrc";
if (-f $configFile)
{
   loadPreferences($configFile);
}

# Figure out which compiler to invoke based on our program name.
$0 =~ m%.*/(.*)$%;
$progName = $1 || $0;

$compiler = $compilerPaths{$progName} || $compilerPaths{"gcc"};

# Get the terminal type. 
$terminal = $ENV{"TERM"} || "dumb";

# If it's in the list of terminal types not to color, or if
# we're writing to something that's not a tty, don't do color.
if (! -t STDOUT || $nocolor{$terminal})
{
   exec $compiler, @ARGV
      or die("Couldn't exec");
}

# Keep the pid of the compiler process so we can get its return
# code and use that as our return code.
$compiler_pid = open3('<&STDIN', \*GCCOUT, '', $compiler, @ARGV);

# Colorize the output from the compiler.
while(<GCCOUT>)
{
   if (m/^(.*?):([0-9]+):(.*)$/) # filename:lineno:message
   {
      $field1 = $1 || "";
      $field2 = $2 || "";
      $field3 = $3 || "";

      if ($field3 =~ m/\s+warning:.*/)
      {
	 # Warning
	 print($colors{"warningFileNameColor"}, "$field1:", color("reset"));
	 print($colors{"warningNumberColor"}, "$field2:", color("reset"));
	 srcscan($field3, $colors{"warningMessageColor"});
      }
      else 
      {
	 # Error
	 print($colors{"errorFileNameColor"}, "$field1:", color("reset"));
	 print($colors{"errorNumberColor"}, "$field2:", color("reset"));
	 srcscan($field3, $colors{"errorMessageColor"});
      }
      print("\n");
   }
   elsif (m/^(.*?):(.+):$/) # filename:message:
   {
      # No line number, treat as an "introductory" line of text.
      srcscan($_, $colors{"introColor"});
   }
   else # Anything else.        
   {
      # Doesn't seem to be a warning or an error. Print normally.
      print(color("reset"), $_);
   }
}

# Get the return code of the compiler and exit with that.
waitpid($compiler_pid, 0);
exit ($? >> 8);





