#ifndef _H_THECONTEXT
#define _H_THECONTEXT

//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// The Original Code is State Machine Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 - 2003 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// Name
//	AppClass
//
// Description
//	When a state map executes an action, it is really calling a
//	member function in the context class.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.6  2015/08/02 19:44:34  cwrapp
// Release 6.6.0 commit.
//
// Revision 1.5  2014/09/06 19:53:15  fperrad
// remove hard tab
//
// Revision 1.4  2005/05/28 13:31:16  cwrapp
// Updated C++ examples.
//
// Revision 1.0  2003/12/14 19:11:44  charlesr
// Initial revision
//

#include "AppClass_sm.h"
#include <map>
#include <string>
#include <vector>

#ifdef CRTP
class AppClass : public AppClassContext<AppClass>
#else
class AppClass
#endif
{
private:
#ifndef CRTP
    AppClassContext _fsm;
#endif

    bool isAcceptable = true;
    std::string number;
    bool correctNumber = true;
        // If a string is acceptable, then this variable is set to YES;
        // NO, otherwise.

public:
    bool sms;
    bool correctStream = true;
    void falseStream() {
        correctStream = false;
    }
    void startAuto() {
        correctStream = true;
        number = "";
        isAcceptable = true;
        numberM = 0;
        numbers.clear();
    }
    std::vector<std::string> numbers;
    int numberK = 0;
    int numberM = 0;
    void kIsNull() { numberK = 0; }
    void kPlusOne() { numberK += 1; }
    void mPlusOne() { numberM += 1; }
    bool prefixFaxTel(std::string str) { return (str == "tel:" || str == "fax:"); }
    bool prefixSMS(std::string str) {
        return str == "sms:";
    }
    bool prefixBody(std::string str) {
        return str == "?body=";
    }
    bool prefixEmpty(std::string str) {
        return str == "";
    }
    AppClass();
        // Default constructor.

    ~AppClass() {};
        // Destructor.

    bool CheckString(std::string);
        // Checks if the string is acceptable.
    void addChar(char _symbol) {
        number += _symbol;
    }
    void addNumber() {
        numbers.push_back(number);
        number = "";
    }
    std::map<std::string, int> m;
    inline void isSMS()
    { sms = true; };
    inline void notSMS()
    { sms = false; };

    bool isCorrectNumber() {
        if (numberK == 11) {
            correctNumber = true;
            return true;
        }
        else {
            correctNumber = false;
            return false;
        }
    }
    bool isCorrectMess() {
        if (numberM <= 64)
        {
            numberM = 0;
            return true;
        }
        else
        {
            numberM = 0;
            return false;
        }
    }

    void Acceptable()
    { isAcceptable = true; 
    for (auto str : numbers) {
        if (m.count(str))
            m[str] += 1;
        else
            m[str] = 1;
    }
    };
    void Unacceptable()
    { isAcceptable = false; };
        // State map actions.
};

#endif
