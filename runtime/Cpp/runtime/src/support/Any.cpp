#include "Any.h"

antlrcpp::Any::~Any()
{
    delete _ptr;
}

antlrcpp::Any::Base::~Base() {
}
