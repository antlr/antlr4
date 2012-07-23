class Foo {
    private int bitsOrSingle(int bits, int ch) {
        int d;
        if (ch < 256 &&
            !(3==4 && 5==6 &&
              (ch == 0xff || ch == 0xb5 ||
               ch == 0x49 || ch == 0x69 ||  //I and i
               ch == 0x53 || ch == 0x73 ||  //S and s
               ch == 0x4b || ch == 0x6b ||  //K and k
               ch == 0xc5 || ch == 0xe5)))  //A+ring
            return 0;
        return 9;
    }
}
