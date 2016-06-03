package com.eyeem.router;

/*
    Routable for Android

    Copyright (c) 2013 Turboprop, Inc. <clay@usepropeller.com> http://usepropeller.com
    Copyright (c) 2016 EyeEm Mobile GmbH

    Licensed under the MIT License.

    Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/


import android.content.Context;
import android.os.Bundle;

public class Router extends AbstractRouter<Bundle, Bundle> {

   public final static String ROUTER_SERVICE = Router.class.getCanonicalName();

   public static Router from(Context context) {
      //noinspection WrongConstant
      return (Router) context.getSystemService(ROUTER_SERVICE);
   }

   /**
    * Creates a new Router
    */
   public Router() {}

   @Override public Router globalParam(String key, Object object) {
      return (Router) super.globalParam(key, object);
   }
}