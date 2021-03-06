// **********************************************************************
//
// Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.5.1
//
// <auto-generated>
//
// Generated from file `laser.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package jderobot;

public final class LaserDataPrxHelper extends Ice.ObjectPrxHelperBase implements LaserDataPrx
{
    public static LaserDataPrx checkedCast(Ice.ObjectPrx __obj)
    {
        LaserDataPrx __d = null;
        if(__obj != null)
        {
            if(__obj instanceof LaserDataPrx)
            {
                __d = (LaserDataPrx)__obj;
            }
            else
            {
                if(__obj.ice_isA(ice_staticId()))
                {
                    LaserDataPrxHelper __h = new LaserDataPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static LaserDataPrx checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        LaserDataPrx __d = null;
        if(__obj != null)
        {
            if(__obj instanceof LaserDataPrx)
            {
                __d = (LaserDataPrx)__obj;
            }
            else
            {
                if(__obj.ice_isA(ice_staticId(), __ctx))
                {
                    LaserDataPrxHelper __h = new LaserDataPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static LaserDataPrx checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        LaserDataPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA(ice_staticId()))
                {
                    LaserDataPrxHelper __h = new LaserDataPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static LaserDataPrx checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        LaserDataPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA(ice_staticId(), __ctx))
                {
                    LaserDataPrxHelper __h = new LaserDataPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static LaserDataPrx uncheckedCast(Ice.ObjectPrx __obj)
    {
        LaserDataPrx __d = null;
        if(__obj != null)
        {
            if(__obj instanceof LaserDataPrx)
            {
                __d = (LaserDataPrx)__obj;
            }
            else
            {
                LaserDataPrxHelper __h = new LaserDataPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static LaserDataPrx uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        LaserDataPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            LaserDataPrxHelper __h = new LaserDataPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::jderobot::LaserData"
    };

    public static String ice_staticId()
    {
        return __ids[1];
    }

    protected Ice._ObjectDelM __createDelegateM()
    {
        return new _LaserDataDelM();
    }

    protected Ice._ObjectDelD __createDelegateD()
    {
        return new _LaserDataDelD();
    }

    public static void __write(IceInternal.BasicStream __os, LaserDataPrx v)
    {
        __os.writeProxy(v);
    }

    public static LaserDataPrx __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            LaserDataPrxHelper result = new LaserDataPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }

    public static final long serialVersionUID = 0L;
}
