#pragma once

//----------------------------------------------------------------------------------------
//	Copyright © 2007 - 2013 Tangible Software Solutions Inc.
//	This class can be used by anyone provided that the copyright notice remains intact.
//
//	This class is used to simulate list constructor calls which reserve the list size.
//----------------------------------------------------------------------------------------
#include <vector>
namespace antlrcpp {
    
    class VectorHelper
    {
    public:
        template<typename T>
        static std::vector<T> VectorWithReservedSize(int size)
        {
            std::vector<T> vector;
            vector.reserve(size);
            return vector;
        }
        
        template<typename T>
        static std::vector<T> VectorSublist(const std::vector<T>& vec, int start, int end)
        {
            std::vector<T> vector(vec.begin() + start, vec.begin() + end);
            return vector;
        }
    };
    
}