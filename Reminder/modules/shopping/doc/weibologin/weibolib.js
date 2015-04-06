(function(a, b) {
    var c = a.SINA,
        d;
    a.SINA = d = {
        VERSION: "0.1"
    }, d.extend = function(a, b, c) {
        if (!b || !a || a === b) return a || null;
        var d;
        if (c)
            for (d in b) a[d] = b[d];
        else
            for (d in b) a.hasOwnProperty(d) || (a[d] = b[d]);
        return a
    }
})(window),
function(a) {
    var b = "array",
        c = "boolean",
        d = "date",
        e = "error",
        f = "function",
        g = "number",
        h = "null",
        i = "object",
        j = "regexp",
        k = "string",
        l = "undefined",
        m = Object.prototype.toString,
        n = Object.prototype.hasOwnProperty,
        o = String.prototype.trim,
        p = String.prototype.trimLeft,
        q = String.prototype.trimRight,
        r = /^\s+|\s+$/g,
        s = /^\s+/g,
        t = /\s+$/g,
        u = "",
        v = !1,
        w = [],
        x = {
            "undefined": l,
            number: g,
            "boolean": c,
            string: k,
            "[object Function]": f,
            "[object RegExp]": j,
            "[object Array]": b,
            "[object Date]": d,
            "[object Error]": e
        },
        y = Array.prototype.slice,
        z = function(a) {
            return y.call(a, 0)
        };
    try {
        y.call(document.documentElement.childNodes, 0)
    } catch (A) {
        z = function(a) {
            var b = [],
                c = a.length,
                d = 0;
            for (; d < c; d++) b[d] = a[d];
            return b
        }
    }
    a.extend(a, {
        isArray: Array.isArray || function(c) {
            return a.type(c) === b
        },
        isBoolean: function(a) {
            return typeof a === c
        },
        isFunction: function(b) {
            return a.type(b) === f
        },
        isDate: function(b) {
            return a.type(b) === d && b.toString() !== "Invalid Date" && !isNaN(b)
        },
        isNull: function(a) {
            return a === null
        },
        isNumber: function(a) {
            return typeof a === g && isFinite(a)
        },
        isObject: function(b) {
            var c = typeof b;
            return b && (c === i || c === f || a.isFunction(b)) || !1
        },
        isPlainObject: function(b) {
            if (!b || a.type(b) !== i || b.nodeType || a.isWindow(b)) return !1;
            if (b.constructor && !n.call(b, "constructor") && !n.call(b.constructor.prototype, "isPrototypeOf")) return !1;
            var c;
            for (c in b);
            return c === undefined || n.call(b, c)
        },
        isEmptyObject: function(b) {
            if (!a.isObject(b)) return !1;
            for (var c in b) return !1;
            return !0
        },
        isString: function(a) {
            return typeof a === k
        },
        isUndefined: function(a) {
            return typeof a === l
        },
        type: function(a) {
            return x[typeof a] || x[m.call(a)] || (a ? i : h)
        },
        isElement: function(a) {
            return !!a && a.nodeType === 1
        },
        isDocument: function(a) {
            return !!a && a.nodeType === 9
        },
        isWindow: function(a) {
            return !!(a && a.alert && a.document)
        },
        each: function(b, c, d) {
            if (!b || !c) return;
            var e, f, g, h = b.length,
                i = h === undefined || a.isFunction(b);
            content = d || window;
            if (i)
                for (e in b) c.call(d, b[e], e, b);
            else
                for (g = 0; g < h; g++) c.call(d, b[g], g, b)
        },
        trim: o ? function(a) {
            return a ? o.call(a) : ""
        } : function(a) {
            return a ? a.toString().replace(r, u) : ""
        },
        trimLeft: p ? function(a) {
            return a ? p.call(a) : ""
        } : function(a) {
            return a ? a.toString().replace(s, u) : ""
        },
        trimRight: q ? function(a) {
            return a ? q.call(a) : ""
        } : function(a) {
            return a ? a.toString().replace(t, u) : ""
        },
        later: function(b) {
            var c, d, e, f, g, h, i, j, k = {
                fn: function() {},
                delay: 0,
                isPeriodic: !1,
                context: null,
                params: []
            };
            return !a.isFunction(b) && a.isObject(b) ? (a.extend(b, k, !1), f = b.fn, g = b.delay || 0, h = !!b.isPeriodic, i = b.params, j = b.context) : (f = arguments[0], g = arguments[1] || 0, h = !!arguments[2], j = arguments[3] || null, i = arguments[4] || []), d = f, j && typeof f == "string" && (d = j[f]), d ? (e = i && i.length ? function() {
                d.apply(j, i)
            } : function() {
                d.call(j)
            }, c = h ? setInterval(e, g) : setTimeout(e, g), {
                id: c,
                periodic: h,
                cancel: function() {
                    this.periodic ? clearInterval(this.id) : clearTimeout(this.id)
                }
            }) : null
        },
        arrayify: function(b) {
            return b === undefined ? [] : a.isArray(b) ? b : b === null || typeof b.length != "number" || a.isString(b) || a.isFunction(b) ? [b] : z(b)
        },
        now: Date.now || function() {
            return (new Date).getTime()
        },
        isReady: !1,
        ready: function(b) {
            v || a._bindReady(), a.isReady ? b.call(window, a) : w.push(b)
        },
        _bindReady: function() {
            var a = this,
                b = document.documentElement.doScroll,
                c = b ? "onreadystatechange" : "DOMContentLoaded",
                d = function() {
                    a._fireReady()
                };
            v = !0;
            if (document.readyState === "complete") return d();
            if (document.addEventListener) {
                function e() {
                    document.removeEventListener(c, e, !1), d()
                }
                document.addEventListener(c, e, !1), window.addEventListener("load", d, !1)
            } else {
                function f() {
                    document.readyState === "complete" && (document.detachEvent(c, f), d())
                }
                document.attachEvent(c, f), window.attachEvent("onload", d);
                if (window == window.top) {
                    function g() {
                        try {
                            b("left"), d()
                        } catch (a) {
                            setTimeout(g, 1)
                        }
                    }
                    g()
                }
            }
        },
        _fireReady: function() {
            if (a.isReady) return;
            a.isReady = !0;
            if (w) {
                var b, c = 0;
                while (b = w[c++]) b.call(window, a);
                w = null
            }
        },
        camelCase: function(a) {
            return a.replace(/-([a-z])/ig, function(a, b) {
                return b.toUpperCase()
            })
        },
        toInt: function(a, b) {
            return b = b || 10, b = parseInt("" + a, b), isNaN(b) ? 0 : b
        },
        toFloat: function(a) {
            return a = parseFloat("" + a), isNaN(a) ? 0 : a
        }
    })
}(SINA),
function(a) {
    a.Array || (a.Array = {});
    var b = Array.prototype.forEach,
        c = Array.prototype.map,
        d = Array.prototype.indexOf,
        e = Array.prototype.lastIndexOf,
        f = Array.prototype.every,
        g = Array.prototype.some,
        h = Array.prototype.filter,
        i = Array.prototype.slice;
    a.extend(a.Array, {
        forEach: b ? function(a, c, d) {
            b.call(a, c, d)
        } : function(a, b, c) {
            var d = 0,
                e = a.length;
            for (; d < e; d++) b.call(c, a[d], d, a)
        },
        map: c ? function(a, b, d) {
            return c.call(a, b, d)
        } : function(b, c, d) {
            var e, f = 0,
                g = b.length;
            if (a.type(c) === "function") {
                e = [];
                for (; f < g; f++) e[f] = c.call(d, b[f], f, b);
                return e
            }
            return i.call(b, 0)
        },
        indexOf: d ? function(a, b, c) {
            return d.call(a, b, c)
        } : function(a, b, c) {
            var d = -1,
                e = a.length;
            typeof c != "number" ? c = 0 : c < 0 && (c = e + c, c < 0 && (c = 0));
            while (c < e) {
                if (a[c] === b) {
                    d = c;
                    break
                }
                c++
            }
            return d
        },
        lastIndexOf: e ? function(a, b, c) {
            return e.call(a, b, c)
        } : function(a, b, c) {
            var d = -1,
                e = a.length;
            typeof c != "number" || c >= e ? c = e - 1 : c < 0 && (c = e + c);
            while (c >= 0) {
                if (a[c] === b) {
                    d = c;
                    break
                }
                c--
            }
            return d
        },
        every: f ? function(a, b, c) {
            return f.call(a, b, c)
        } : function(a, b, c) {
            var d = !0,
                e = 0,
                f = a.length;
            for (; e < f; e++)
                if (!b.call(c, a[e], e, a)) {
                    d = !1;
                    break
                }
            return d
        },
        some: g ? function(a, b, c) {
            return g.call(a, b, c)
        } : function(a, b, c) {
            var d = !1,
                e = 0,
                f = a.length;
            for (; e < f; e++)
                if (b.call(c, a[e], e, a)) {
                    d = !0;
                    break
                }
            return d
        },
        filter: h ? function(a, b, c) {
            return h.call(a, b, c)
        } : function(a, b, c) {
            var d = [],
                e = 0,
                f = a.length;
            for (; e < f; e++) b.call(c, a[e], e, a) && d.push(a[e]);
            return d
        },
        remove: function(b, c, d) {
            var e = b.length - 1,
                f = a.type(c);
            if (f == "number") c >= 0 && c <= e && b.splice(c, 1);
            else if (f == "function")
                while (e >= 0) c.call(d, b[e], e, b) && b.splice(e, 1), e--;
            return b
        }
    })
}(SINA),
function(a) {
    var b = navigator && navigator.userAgent || "",
        c = location && location.href || "",
        d = /windows|win32/i,
        e = /macintosh/i,
        f = /AppleWebKit\/([^\s]*)/,
        g = /Presto\/([^\s]*)/,
        h = /Gecko\/([^\s]*)/,
        i = /rv:([^\s\)]*)/,
        j = /Trident\/([^\s]*)/,
        k = /MSIE\s([^;]*)/,
        l = /Firefox\/([^\s]*)/,
        m = /Chrome\/([^\s]*)/,
        n = /\/([^\s]*) Safari/,
        o = /Opera[\s\/]([^\s]*)/,
        p = /Opera Mini[^;]*/,
        q = /AdobeAIR\/([^\s]*)/,
        r = / Mobile\//,
        s = /OS ([^\s]*)/,
        t = /iPad|iPod|iPhone/,
        u = /NokiaN[^\/]*|Android \d\.\d|webOS\/\d\.\d/,
        v = /Android ([^\s]*);/,
        w = /webOS\/([^\s]*);/,
        x = /NokiaN[^\/]*/,
        y = /KHTML/,
        z, A = function(a) {
            var b = 0;
            return parseFloat(a.replace(/\./g, function() {
                return b++, b === 1 ? "." : ""
            }))
        },
        B = {
            trident: 0,
            gecko: 0,
            webkit: 0,
            presto: 0,
            ie: 0,
            firefox: 0,
            chrome: 0,
            safari: 0,
            opera: 0,
            mobile: null,
            https: !1,
            os: null
        };
    B.https = c.toLowerCase().indexOf("https") === 0, b && (d.test(b) ? B.os = "windows" : e.test(b) ? B.os = "mac" : B.os = "other", z = b.match(k), z && z[1] ? (B.trident = 1, B.ie = A(z[1]), z = b.match(j), z && z[1] && (B.trident = A(z[1]))) : (z = b.match(h), z ? (B.gecko = 1, z = b.match(i), z && z[1] && (B.gecko = A(z[1])), z = b.match(l), z && z[1] && (B.firefox = A(z[1]))) : (z = b.match(f), z && z[1] ? (B.webkit = A(z[1]), z = b.match(m), z && z[1] ? B.chrome = A(z[1]) : (z = b.match(n), z && z[1] && (B.safari = A(z[1]))), r.test(b) ? (z = b.match(t), z && z[0] && (B.mobile = z[0].toLowerCase())) : (z = b.match(u), z && z[0] && (B.mobile = z[0].toLowerCase()))) : (z = b.match(g), z && z[1] && (B.presto = A(z[1]), z = b.match(o), z && z[1] && (B.opera = A(z[1]), z = b.match(p), z && (B.mobile = z[0]))))))), a.UA = B
}(SINA),
function(a) {
    function t(a, b) {
        for (var c in a)
            if (s[a[c]] !== undefined && (!b || b(a[c], r))) return !0
    }

    function u(a, b) {
        var c = a.charAt(0).toUpperCase() + a.substr(1),
            d = [a, "Webkit" + c, "Moz" + c, "O" + c, "ms" + c, "Khtml" + c];
        return !!t(d, b)
    }

    function v(a) {
        s.cssText = a
    }

    function w(a, b) {
        return v(p.join(a + ";") + (b || ""))
    }
    if (a.support) return;
    var b, c = document,
        d = c.documentElement,
        e = c.documentElement,
        f = c.createElement("script"),
        g = c.createElement("div"),
        h = "script" + +(new Date),
        i = function(a, b) {
            for (var c in a) b(a[c], c, a)
        };
    g.style.display = "none", g.innerHTML = "   <link/><table></table><a href='/a' style='color:red;float:left;opacity:.55;'>a</a><input type='checkbox'/>";
    var j = g.getElementsByTagName("*"),
        k = g.getElementsByTagName("a")[0],
        l = c.createElement("select"),
        m = l.appendChild(c.createElement("option"));
    if (!j || !j.length || !k) return;
    b = {
        byClassName: !!c.getElementsByClassName,
        leadingWhitespace: g.firstChild.nodeType === 3,
        tbody: !g.getElementsByTagName("tbody").length,
        htmlSerialize: !!g.getElementsByTagName("link").length,
        style: /red/.test(k.getAttribute("style")),
        hrefNormalized: k.getAttribute("href") === "/a",
        opacity: /^0.55$/.test(k.style.opacity),
        cssFloat: !!k.style.cssFloat,
        checkOn: g.getElementsByTagName("input")[0].value === "on",
        optSelected: m.selected,
        optDisabled: !1,
        checkClone: !1,
        scriptEval: !1,
        noCloneEvent: !0
    }, l.disabled = !0, b.optDisabled = !m.disabled, f.type = "text/javascript";
    try {
        f.appendChild(c.createTextNode("window." + h + "=1;"))
    } catch (n) {}
    e.insertBefore(f, e.firstChild), window[h] && (b.scriptEval = !0, delete window[h]), e.removeChild(f), g.attachEvent && g.fireEvent && (g.attachEvent("onclick", function x() {
        b.noCloneEvent = !1, g.detachEvent("onclick", x)
    }), g.cloneNode(!0).fireEvent("onclick")), g = c.createElement("div"), g.innerHTML = "<input type='radio' name='radiotest' checked='checked'/>";
    var o = c.createDocumentFragment();
    o.appendChild(g.firstChild), b.checkClone = o.cloneNode(!0).cloneNode(!0).lastChild.checked, e = f = g = j = k = null, b.video = function() {
        var a = c.createElement("video"),
            b = !!a.canPlayType;
        return b && (b = new Boolean(b), b.ogg = a.canPlayType('video/ogg; codecs="theora"'), b.h264 = a.canPlayType('video/mp4; codecs="avc1.42E01E"'), b.webm = a.canPlayType('video/webm; codecs="vp8, vorbis"')), !!b
    }(), b.audio = function() {
        var a = c.createElement("audio"),
            b = !!a.canPlayType;
        return b && (b = new Boolean(b), b.ogg = a.canPlayType('audio/ogg; codecs="vorbis"'), b.mp3 = a.canPlayType("audio/mpeg;"), b.wav = a.canPlayType('audio/wav; codecs="1"'), b.m4a = a.canPlayType("audio/x-m4a;") || a.canPlayType("audio/aac;")), !!b
    }(), b.canvas = !!c.createElement("canvas").getContext, b.canvasText = !!b.canvas && typeof c.createElement("canvas").getContext("2d").fillText == "function", b.postMessage = !!window.postMessage, b.localStorage = function() {
        try {
            return "localStorage" in window && window.localStorage !== null
        } catch (a) {
            return !1
        }
    }(), b.sessionStorage = function() {
        try {
            return "sessionStorage" in window && window.sessionStorage !== null
        } catch (a) {
            return !1
        }
    }(), b.WebSocket = "WebSocket" in window;
    var p = " -o- -moz- -ms- -webkit- -khtml- ".split(" "),
        q = "modernizr",
        r = c.createElement(q),
        s = r.style;
    i({
        cssAnimation: "animationName",
        cssTransition: "transitionProperty",
        cssBackgroundSize: "backgroundSize",
        cssBoxShadow: "boxShadow",
        cssBorderImage: "borderImage",
        cssColumnCount: "columnCount",
        cssBoxReflect: "boxReflect"
    }, function(a, c) {
        b[c] = u(a)
    }), b.cssBorderRadius = u("borderRadius", "", function(a) {
        return ("" + a).indexOf("orderRadius") > -1
    }), b.cssMultipleBackground = function() {
        return v("background:url(//:),url(//:),red url(//:)"), (new RegExp("(url\\s*\\(.*?){3}")).test(s.background)
    }(), b.cssRGBA = function() {
        return v("background-color:rgba(150,255,150,.5)"), ("" + s.backgroundColor).indexOf("rgba") > -1
    }(), b.cssTransform = function() {
        return !!t(["transformProperty", "WebkitTransform", "MozTransform", "OTransform", "msTransform"])
    }(), b.cssTransform3d = function() {
        var a = !!t(["perspectiveProperty", "WebkitPerspective", "MozPerspective", "OPerspective", "msPerspective"]);
        if (a) {
            var b = document.createElement("style"),
                e = c.createElement("div");
            b.textContent = "@media (" + p.join("transform-3d),(") + "modernizr){#modernizr{height:3px}}", c.getElementsByTagName("head")[0].appendChild(b), e.id = "modernizr", d.appendChild(e), a = e.offsetHeight === 3, b.parentNode.removeChild(b), e.parentNode.removeChild(e)
        }
        return a
    }(), s = r = null, a.support = b
}(SINA),
function(a) {
    function b(a, b, c) {
        b = b || 1;
        var d = 0;
        for (; a; a = a[c])
            if (a.nodeType === 1 && ++d === b) break;
        return a
    }

    function c(a, b) {
        var c = [];
        for (; a; a = a.nextSibling) a.nodeType === 1 && a !== b && c.push(a);
        return c
    }
    a.DOM || (a.DOM = {}), a.extend(a.DOM, {
        byId: function(a) {
            return typeof a == "string" ? document.getElementById(a) : a
        },
        next: function(c) {
            return a.isElement(c) ? b(c, 2, "nextSibling") : null
        },
        previous: function(c) {
            return a.isElement(c) ? b(c, 2, "previousSibling") : null
        },
        parent: function(b) {
            return a.isElement(b) && (b = b.parentNode) && b.nodeType !== 11 ? b : null
        },
        children: function(b) {
            return a.isElement(b) ? c(b.firstChild) : []
        },
        siblings: function(b) {
            return a.isElement(b) ? c(b.parentNode.firstChild, b) : []
        },
        contains: document.documentElement.contains ? function(b, c) {
            return (a.isElement(b) || a.isDocument(b)) && a.isElement(c) && b !== c && (b.contains ? b.contains(c) : !0)
        } : function(b, c) {
            return (a.isElement(b) || a.isDocument(b)) && a.isElement(c) && !!(b.compareDocumentPosition(c) & 16)
        }
    })
}(SINA),
function(a) {
    function G(a, b) {
        return a[b]
    }

    function H(a, b, c, d) {
        c++;
        if (c >= d) return a;
        var e = b[c];
        return H(e.get(a, O(e.chkAttr)), b, c, d)
    }

    function I(a, b, c) {
        var d = b[c],
            e = d.rel,
            f = [],
            g = 0,
            h, i, j, k;
        h = a.length;
        if (c < 1) return a;
        for (; g < h; g++) {
            i = c, k = j = a[g];
            while (k && i > 0) d = b[--i], d.chkAll && (k = J[e](k, d.chkAll)), e = d.rel;
            k && f.push(j)
        }
        return f
    }

    function N(a) {
        return M.hasOwnProperty(a) ? M[a] : M(a)
    }

    function O() {
        var a = [],
            b = 0,
            c = arguments.length,
            d, e, f;
        for (; b < c; b++) {
            f = arguments[b], d = 0, e = f.length;
            for (; d < e; d++) a.push(f[d])
        }
        return c = a.length, c < 1 ? null : c == 1 ? a[0] : function(b) {
            var d = 0;
            for (; d < c; d++)
                if (!a[d](b)) return !1;
            return !0
        }
    }

    function P(a, b) {
        return function(c) {
            return b === c[a]
        }
    }

    function R(a, b, c) {
        var d = a.rel,
            e = a.id,
            f = a.tagName == "*" ? "" : a.tagName,
            g = a.className,
            h = a.chkLeft,
            i = a.chkAttr,
            j = a.getBy,
            k, l;
        if (d == " " || a.isStartLevel && b == c) {
            a.get = j == "*" ? K[j] : K[j](a[j]);
            switch (j) {
                case "id":
                    b < c && h.push(P("id", e)), f && i.push(P("tagName", f)), g && i.push(E("class", g, "~="));
                    break;
                case "tagName":
                    b < c && h.push(P("tagName", f)), g && i.push(E("class", g, "~="));
                    break;
                case "className":
                    b < c && h.push(E("class", g, "~="));
                    break;
                default:
                    i.unshift(P("nodeType", 1)), g && i.push(E("class", g, "~="))
            }
        } else a.get = K[d], e && i.unshift(P("id", e)), f && i.push(P("tagName", f)), g && i.push(E("class", g, "~="));
        return a.chkAll = O(h, i), a
    }

    function S(a) {
        var b = [],
            c = "",
            d = "",
            e = "",
            f = 0,
            g = a.length,
            h = 0,
            i = 0,
            j = 0,
            k = " ",
            l = null,
            m = null;
        while (f < g) {
            c = a.charAt(f);
            if (c == "[") {
                e = a.slice(f), i = U(e, m);
                if (!i) return [];
                f += i
            } else if (!/\s/.test(c)) {
                "+>~".indexOf(c) > -1 ? f++ : c = " ", m = {
                    isStartLevel: !1,
                    rel: c,
                    id: "",
                    tagName: "*",
                    className: "",
                    chkLeft: [],
                    chkAttr: []
                }, i = T(a.slice(f), m);
                if (!i) return [];
                m.id ? b = [m] : b.push(m), l = m, f += i
            } else f++
        }
        return b
    }

    function T(a, b) {
        var c, d = 0,
            f = "",
            g, l;
        c = a.match(e);
        if (c) {
            d = c[0].length, c = c[1];
            if (k.test(c)) {
                if (!D.hasOwnProperty(RegExp.$1)) return 0;
                b.chkAttr.push(D[RegExp.$1])
            }
            l = b.rel, h.test(c) && (b.id = RegExp.$1), i.test(c) && (b.tagName = RegExp.$1.toUpperCase()), j.test(c) && (b.className = RegExp.$1)
        }
        return d
    }

    function U(a, b) {
        var c;
        return (c = a.match(g)) ? (b.chkAttr.push(E(c[1], c[4], c[2])), c[0].length) : (c = a.match(f)) ? (b.chkAttr.push(E(c[1])), c[0].length) : 0
    }

    function V(a, b) {
        var c, d, e = 0,
            f, g, h = 0,
            i = 0,
            j = 0,
            k, f, m, n, o, p = g = b.getElementsByTagName("*").length;
        d = S(a), e = d.length;
        if (e < 1) return [];
        k = e - 1;
        for (h = e - 1; h >= 0; h--) {
            f = d[h], f.id ? (j = document.getElementById(f.id) ? 1 : 0, f.getBy = "id") : f.tagName != "*" ? (j = b.getElementsByTagName(f.tagName).length, f.getBy = "tagName", f.className && l && (n = b.getElementsByClassName(f.className).length, n < j && (j = n, f.getBy = "className"))) : f.className && l ? (j = b.getElementsByClassName(f.className).length, f.getBy = "className") : (j = p, f.getBy = "*");
            if (j < 1) return [];
            j = j * (f.chkAttr.length + 1), g > j * 1.5 && (g = j, k = h)
        }
        o = d[k], o.isStartLevel = !0;
        for (h = 0; h < e; h++) R(d[h], h, k);
        return m = o.get([b], O(o.chkAttr)), m = I(m, d, k), m = H(m, d, k, d.length), m
    }
    var b = /^#([\w\$\-]+)$/,
        c = /^[\w]+$/,
        d = /^([\w\$\-\*]*)\.(\w+)$/,
        e = /^\s*([\w\.\#\:\$\-]+)/,
        f = /^\[([\w]+)]/,
        g = /^\[([\w]+)([\~\^\$\*\|\!]?\=)([\'\"]?)(.*?)\3\]/,
        h = /\#([\w\$\-]+)/,
        i = /^(\w+)/,
        j = /\.([\w\$\-]+)/,
        k = /\:([\w\$\-]+)/,
        l = a.support.byClassName,
        m = Object.prototype.hasOwnProperty,
        n = Array.prototype.slice,
        o = Array.prototype.push,
        p = a.trim,
        q = function(a, b) {
            var c = [],
                d = 0,
                e = a.length;
            for (; d < e; d++) b(a[d]) && c.push(a[d]);
            return c
        },
        r = a.Array.every,
        s = a.DOM,
        t = s.hasClass,
        u = s.getAttr,
        v = function(a, b, c, d) {
            var e = 0;
            for (; a; a = a[c])
                if (a.nodeType === 1 && ++e === d) break;
            return b(a) ? a : null
        },
        w = a.DOM.next,
        x = function(a, b, c) {
            var d = a.childNodes,
                e, f = 0,
                g = x.length;
            for (; e < g; e++) a = d[e], a.nodeType === 1 && b(a) && c.push(a);
            return c
        },
        y = s.contains,
        z = function(a) {
            return a.nodeType == 1
        },
        A = a.Array.forEach,
        B = function(a) {
            var b = [],
                c = 0,
                d = a.length;
            for (; c < d; c++) b[c] = a[c];
            return b
        },
        C = Array.lastIndexOf || function(a, b) {
            var c = a.length - 1;
            for (; c >= 0; c--)
                if (b === a[c]) return c;
            return -1
        },
        D, E, F;
    D = {
        enabled: function(a) {
            return a.disabled === !1 && a.type !== "hidden"
        },
        disabled: function(a) {
            return a.disabled === !0
        },
        checked: function(a) {
            return a.checked === !0
        },
        selected: function(a) {
            return a.parentNode.selectedIndex, a.selected === !0
        },
        parent: function(a) {
            return !!a.firstChild
        },
        empty: function(a) {
            return !a.firstChild
        },
        header: function(a) {
            return /h\d/i.test(a.nodeName)
        },
        text: function(a) {
            return "text" === a.type
        },
        radio: function(a) {
            return "radio" === a.type
        },
        checkbox: function(a) {
            return "checkbox" === a.type
        },
        file: function(a) {
            return "file" === a.type
        },
        password: function(a) {
            return "password" === a.type
        },
        submit: function(a) {
            return "submit" === a.type
        },
        image: function(a) {
            return "image" === a.type
        },
        reset: function(a) {
            return "reset" === a.type
        },
        button: function(a) {
            return "button" === a.type || a.nodeName.toLowerCase() === "button"
        },
        input: function(a) {
            return /input|select|textarea|button/i.test(a.nodeName)
        }
    }, G["class"] = function(a) {
        return a.className
    }, G["for"] = function(a) {
        return a.htmlFor
    }, a.support.hrefNormalized || (G.href = function(a, b) {
        return a.getAttribute("href", 2)
    });
    var J = {};
    J[" "] = function(a, b) {
        var c = null,
            d = a;
        while (d = d.parentNode)
            if (b(d)) return d;
        return null
    }, J[">"] = function(a, b) {
        return (a = a.parentNode) && b(a) ? a : null
    }, J["+"] = function(a, b) {
        return (a = v(a, b, "previousSibling", 2)) ? a : null
    }, J["~"] = function(a, b) {
        while (a = a.previousSibling)
            if (1 === node.nodeType && b(a)) return a;
        return null
    };
    var K = {};
    K[">"] = function(a, b) {
        var c, d, e, f = [];
        for (c = 0, d = a.length; c < d; c++) {
            e = a[c].firstChild;
            while (e) 1 === e.nodeType && b(e) && f.push(e), e = e.nextSibling
        }
        return f
    }, K["+"] = function(a, b) {
        var c, d, e, f, g = [];
        if (!b)
            for (c = 0, d = a.length; c < d; c++)(f = w(a[c])) && g.push(f);
        else
            for (c = 0, d = a.length; c < d; c++)(f = w(a[c])) && b(f) && g.push(f);
        return g
    }, K["~"] = function(a, b) {
        a = F["~"](a);
        var c, d, e, f = [];
        if (!b)
            for (c = 0, d = a.length; c < d; c++) {
                e = a[c];
                while (e = e.nextSibling) 1 === e.nodeType && f.push(e)
            } else
                for (c = 0, d = a.length; c < d; c++) {
                    e = a[c];
                    while (e = e.nextSibling) 1 === e.nodeType && b(e) && f.push(e)
                }
        return f
    }, K["*"] = function(a, b) {
        a = F[" "](a);
        var c, d, e, f, g, h, i = [];
        f = a.length;
        if (f < 1) return i;
        if (!b)
            for (c = 0; c < f; c++) {
                g = a[c].getElementsByTagName("*");
                for (d = 0, e = g.length; d < e; d++) 1 == g[d].nodeType && i.push(g[d])
            } else
                for (c = 0; c < f; c++) {
                    g = a[c].getElementsByTagName("*");
                    for (d = 0, e = g.length; d < e; d++) h = g[d], 1 == h.nodeType && b(h) && i.push(h)
                }
        return i
    }, K.id = function(a) {
        return function(b, c) {
            return b = document.getElementById(a), c ? b && c(b) ? [b] : [] : b ? [b] : []
        }
    }, K.tagName = function(a) {
        return function(b, c) {
            b = F[" "](b);
            var d, e, f, g = b.length,
                h, i, j = [];
            if (g < 1) return j;
            if (!c)
                for (d = 0; d < g; d++) {
                    h = b[d].getElementsByTagName(a);
                    for (e = 0, f = h.length; e < f; e++) j.push(h[e])
                } else
                    for (d = 0; d < g; d++) {
                        h = b[d].getElementsByTagName(a);
                        for (e = 0, f = h.length; e < f; e++) i = h[e], c(i) && j.push(i)
                    }
            return j
        }
    }, !l || (K.className = function(a) {
        return function(b, c) {
            b = F[" "](b);
            var d, e, f, g = b.length,
                h, i, j = [];
            if (g < 1) return j;
            if (!c)
                for (d = 0; d < g; d++) {
                    h = b[d].getElementsByClassName(a);
                    for (e = 0, f = h.length; e < f; e++) j.push(h[e])
                } else
                    for (d = 0; d < g; d++) {
                        h = b[d].getElementsByClassName(a);
                        for (e = 0, f = h.length; e < f; e++) i = h[e], c(i) && j.push(i)
                    }
            return j
        }
    });
    var L = document.documentElement,
        M;
    L && (L.hasAttribute ? M = function(a) {
        return function(b) {
            return b.hasAttribute(a)
        }
    } : (M = function(a) {
        return function(b) {
            return b.getAttribute(a) !== ""
        }
    }, M["class"] = function(a) {
        return a.getAttribute("className") !== ""
    })), M || (M = function(a, b) {
        return function(a) {
            return a.hasOwnProperty(b)
        }
    }), E = function(a, b, c) {
        if (arguments.length == 1) return N(a);
        var d = G.hasOwnProperty(a) ? G[a] : G,
            e = E[c](b);
        return function(b) {
            return e(d(b, a))
        }
    }, E.tagName = function(a) {
        return function(b) {
            return a == b.tagName
        }
    }, E["class"] = E.className = function(a) {
        return a = " " + p(a) + " ",
            function(b) {
                return (" " + b.className + " ").indexOf(a) > -1
            }
    }, a.extend(E, {
        "=": function(a) {
            return function(b) {
                return b == a
            }
        },
        "!=": function(a) {
            return function(b) {
                return b != a
            }
        },
        "~=": function(a) {
            return a = " " + p(a) + " ",
                function(b) {
                    return (" " + b + " ").indexOf(a) > -1
                }
        },
        "|=": function(a) {
            var b = a + "-",
                c = b.length;
            return function(d) {
                return d == a || d.slice(0, c) == b
            }
        },
        "^=": function(a) {
            var b = a.length;
            return function(c) {
                return c.slice(0, b) == a
            }
        },
        "$=": function(a) {
            var b = -a.length;
            return function(c) {
                return c.slice(b) == a
            }
        },
        "*=": function(a) {
            return function(b) {
                return b.indexOf(a) > -1
            }
        }
    });
    var Q = {
        "=": 1,
        "!=": 1,
        "~=": 4,
        "|=": 2,
        "^=": 2,
        "$=": 2,
        "*=": 2
    };
    F = {}, F[" "] = function(a) {
        var b, c = a.length,
            d, e;
        if (c < 2) return a;
        b = 1, d = a[0], e = [d];
        for (; b < c; b++) y(d, a[b]) || (d = a[b], e.push(d));
        return e
    }, F[">"] = function(a) {
        return a
    }, F["+"] = function(a) {
        return a
    }, F["~"] = function(a) {
        var b = -1,
            c = 0,
            d = a.length,
            e, f = [],
            g = [];
        for (; c < d; c++)(e = a[c].parentNode) && C(f, e) < 0 && (f.push(e), g.push(a[c]));
        return g
    }, a.extend(a, {
        query: function(a, b) {
            return arguments.length < 1 ? [document] : (b = b || document.body, V(a, b))
        }
    })
}(SINA),
function(a) {
    var b = a.DOM,
        c = /[\n\t]/g,
        d = /\s+/,
        e = /\r/g,
        f = /^(?:href|src|style)$/,
        g = /^(?:button|input)$/i,
        h = /^(?:button|input|object|select|textarea)$/i,
        i = /^a(?:rea)?$/i,
        j = /^(?:radio|checkbox)$/i,
        k = a.UA.ie ? "innerText" : "textContent",
        l = {
            "for": "htmlFor",
            "class": "className",
            readonly: "readOnly",
            maxlength: "maxLength",
            cellspacing: "cellSpacing",
            rowspan: "rowSpan",
            colspan: "colSpan",
            tabindex: "tabIndex",
            usemap: "useMap",
            frameborder: "frameBorder",
            innerText: k,
            textContent: k
        },
        m = {},
        n, o, p;
    a.support.style || (m.style = {
        get: function(a) {
            return a.style.cssText
        },
        set: function(a, b) {
            a.style.cssText = "" + b
        }
    }), m[k] = {
        get: function(a) {
            return a[k]
        },
        set: function(a, b) {
            a[k] = "" + b
        }
    }, m.className = {
        get: function(a) {
            return a.className
        },
        set: function(a, b) {
            a.className = "" + b
        }
    }, m.innerHTML = {
        get: function(a) {
            return a.innerHTML
        },
        set: function(a, c) {
            b.html(a, c)
        }
    }, n = function(b, c) {
        c = l[c] || c;
        var d = m[c],
            e;
        return d && "get" in d ? d.get(b) : !b.attributes[c] && b.hasAttribute && !b.hasAttribute(c) ? undefined : !a.support.hrefNormalized && f.test(c) ? b.getAttribute(c, 2) : b.getAttributeNode(c) ? b.getAttributeNode(c).nodeValue : b.getAttribute(c)
    }, o = function(a, b, c) {
        b = l[b] || b;
        var d = m[b];
        if (d && "set" in d) {
            d.set(a, c);
            return
        }
        if ((b in a || a[b] !== undefined) && !f.test(b)) {
            if (b === "type" && g.test(a.nodeName) && a.parentNode) return;
            c === null ? a.nodeType === 1 && a.removeAttribute(b) : a.getAttributeNode(b) ? a.getAttributeNode(b).nodeValue = c : a[b] = c
        } else a.setAttribute(b, "" + c)
    }, p = function(b, c, d) {
        if (!b || b.nodeType === 3 || b.nodeType === 8) return undefined;
        var e = a.isPlainObject(c) ? 2 : d !== undefined ? 1 : 0,
            f;
        if (!e) return n(b, c);
        if (e == 1) o(b, c, d);
        else
            for (f in c) o(b, f, c[f])
    }, a.extend(a.DOM, {
        attr: p,
        removeAttr: function(b, c) {
            a.DOM.attr(b, c, null)
        },
        getAttr: n,
        setAttr: o
    })
}(SINA),
function(a) {
    function w(b, c, d) {
        if (!b || b.nodeType === 3 || b.nodeType === 8 || !b.style) return;
        if (typeof c == "string")
            if (arguments.length > 2) t(b, c, d);
            else return s(b, c);
        else if (a.isPlainObject(c))
            for (var e in c) t(b, e, c[e])
    }
    var b = a.each,
        c = a.DOM.contains,
        d = a.camelCase,
        e = function(a, b, c) {
            var d = {};
            for (var e in b) d[e] = a.style[e], a.style[e] = b[e];
            c.call(a);
            for (e in b) a.style[e] = d[e]
        },
        f = /alpha\([^)]*\)/i,
        g = /opacity=([^)]*)/,
        h = /-([a-z])/ig,
        i = /([A-Z])/g,
        j = /^-?\d+(?:px)?$/i,
        k = /^-?\d/,
        l = {
            "float": a.support.cssFloat ? "styleFloat" : "cssFloat"
        },
        m = {},
        n = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        },
        o = ["Left", "Right"],
        p = ["Top", "Bottom"],
        q = {
            zIndex: !0,
            fontWeight: !0,
            opacity: !0,
            zoom: !0,
            lineHeight: !0
        },
        r, s, t, u, v;
    a.support.opacity || (m.opacity = {
        get: function(a, b) {
            return g.test((b && a.currentStyle ? a.currentStyle.filter : a.style.filter) || "") ? parseFloat(RegExp.$1) / 100 + "" : b ? "1" : ""
        },
        set: function(a, b) {
            var c = a.style;
            c.zoom = 1;
            var d = isNaN(b) ? "" : "alpha(opacity=" + b * 100 + ")",
                e = c.filter || "";
            c.filter = f.test(e) ? e.replace(f, d) : c.filter + " " + d
        }
    }), b(["height", "width"], function(a) {
        m[a] = {
            get: function(b, c, d) {
                var f;
                if (c) return b.offsetWidth !== 0 ? f = v(b, a, d) : e(b, n, function() {
                    f = v(b, a, d)
                }), f + "px"
            },
            set: function(a, b) {
                if (!j.test(b)) return b;
                b = parseFloat(b);
                if (b >= 0) return b + "px"
            }
        }
    }), r = function(a, b, c) {
        var e, f = d(b),
            g = a.style,
            h = m[f];
        return b = l[f] || f, h && "get" in h && (e = h.get(a, !1, c)) !== undefined ? e : g[b]
    }, v = function(a, c, d) {
        var e = c === "width" ? o : p,
            f = c === "width" ? a.offsetWidth : a.offsetHeight;
        return d === "border" ? f : (b(e, function(b) {
            d || (f -= parseFloat(s(a, "padding" + b)) || 0), d === "margin" ? f += parseFloat(s(a, "margin" + b)) || 0 : f -= parseFloat(s(a, "border" + b + "Width")) || 0
        }), f)
    }, document.defaultView && document.defaultView.getComputedStyle ? u = function(a, b) {
        var d, e, f;
        b = b.replace(i, "-$1").toLowerCase();
        if (!(e = a.ownerDocument.defaultView)) return undefined;
        if (f = e.getComputedStyle(a, null)) d = f.getPropertyValue(b), d === "" && !c(a.ownerDocument.documentElement, a) && (d = r(a, b));
        return d
    } : document.documentElement.currentStyle && (u = function(a, b) {
        var c, d, e = a.currentStyle && a.currentStyle[b],
            f = a.style;
        return !j.test(e) && k.test(e) && (c = f.left, d = a.runtimeStyle.left, a.runtimeStyle.left = a.currentStyle.left, f.left = b === "fontSize" ? "1em" : e || 0, e = f.pixelLeft + "px", f.left = c, a.runtimeStyle.left = d), e
    }), s = function(a, b, c) {
        var e, f = d(b),
            g = m[f];
        return b = l[f] || f, g && "get" in g && (e = g.get(a, !0, c)) !== undefined ? e : u ? u(a, b, f) : a.style[b]
    }, t = function(a, b, c) {
        if (typeof c == "number" && isNaN(c) || c == null) return;
        b = d(b), typeof c == "number" && !q[b] && (c += "px");
        var e = m[b];
        if (!e || !("set" in e) || (c = e.set(a, c)) !== undefined) try {
            a.style[b] = c
        } catch (f) {}
    }, a.extend(a.DOM, {
        style: w,
        getStyle: s,
        setStyle: t,
        width: function(a, b) {
            if (arguments.length < 2) return v(a, "width");
            t(a, "width", b)
        },
        height: function(a, b) {
            if (arguments.length < 2) return v(a, "height");
            t(a, "height", b)
        },
        size: function(b, c) {
            if (arguments.length < 2) return {
                width: v(b, "width"),
                height: v(b, "height")
            };
            a.isObject(c) && (c.hasOwnProperty("width") && t(b, "width", c.width), c.hasOwnProperty("height") && t(b, "height", c.width))
        }
    })
}(SINA),
function(a) {
    function b(a) {
        return a && typeof a == "string" && /^[a-zA-Z\$][\w\d\-\$]*$/.test(a)
    }
    a.extend(a.DOM, {
        hasClass: function(a, c) {
            return !!a.className && b(c) && (" " + a.className + " ").indexOf(" " + c + " ") > -1
        },
        addClass: function(c, d) {
            if (!a.isElement(c) || !b(d)) return;
            c.className ? a.DOM.hasClass(c, d) || (c.className += " " + d) : c.className = d
        },
        removeClass: function(c, d) {
            a.isElement(c) && b(d) && a.DOM.hasClass(c, d) && (c.className = a.Array.remove(c.className.split(/\s+/), function(a) {
                return a == d
            }).join(" "))
        }
    })
}(SINA),
function(a) {
    function d(b, c, d) {
        var e = b.childNodes,
            f = document.createElement("div"),
            g, h;
        for (g = e.length - 1; g >= 0; g--) e[g].parentNode.removeChild(e[g]);
        f.innerHTML = "<table><" + d + ">" + c + "</" + d + "></table>", e = a.arrayify(f.getElementsByTagName(d)[0].childNodes);
        for (g = 0, h = e.length; g < h; g++) b.appendChild(e[g]);
        e = f = null
    }

    function e(a, b) {
        var c, d, f, g;
        c = a.nodeName;
        if (e.hasOwnProperty(c)) e[c](a, b);
        else {
            c = document.createElement("div"), c.innerHTML = b, d = c.childNodes, a.innerHTML = "";
            for (f = 0, g = d.length; f < g; f++) a.appendChild(d[f]);
            c = null
        }
    }
    var b = a.UA.ie ? "innerText" : "textContent",
        c = a.UA;
    e.TABLE = function(a, b) {
        var c = document.createElement("div"),
            e, f = a.getElementsByTagName("tbody");
        f.length > 0 ? f = f[0] : f = a.appendChild(document.createElement("tbody")), b = b.replace(/^\s*<tbody[^>]*>((?:.|\n)*?)<\/tbody>\s*$/ig, "$1"), d(f, b, "TBODY");
        return
    }, e.THEAD = function(a, b) {
        d(a, b, "THEAD")
    }, e.TFOOT = function(a, b) {
        d(a, b, "TFOOT")
    }, e.TR = function(a, b) {
        d(a, b, "TR")
    }, e.STYLE = function(a, b) {
        return
    }, e.SCRIPT = function(a, b) {
        a.text = b
    }, a.extend(a.DOM, {
        text: function(d, e) {
            var f = "SCRIPT" == d.nodeName,
                g = f && c.ie ? "text" : b;
            if (!(arguments.length > 1)) return a.isElement(d) && d[g] ? d[g] : "";
            if (typeof e != "string" || !a.isElement(d)) return;
            d[g] = e
        },
        html: function(b, c) {
            if (!(arguments.length > 1)) return a.isElement(b) && b.innerHTML ? b.innerHTML : "";
            if (typeof c != "string" || !a.isElement(b)) return;
            var d, f, g = a.DOM.remove;
            d = b.getElementsByTagName("*");
            for (f = d.length - 1; f >= 0; f--) try {
                g(d[f])
            } catch (h) {}
            d = null;
            try {
                b.innerHTML = c
            } catch (h) {
                e(b, c)
            }
        },
        value: function(b, c) {
            if (!(arguments.length > 1)) return a.isElement(b) && b.value ? b.value : "";
            if (typeof c != "string" || !a.isElement(b)) return;
            b.value = c
        }
    })
}(SINA),
function(a) {
    a.extend(a.DOM, {
        create: function(b, c) {
            if (c && !a.isElement(c) || typeof b != "string") return;
            c || (c = document.body);
            var d = document.createElement(b);
            c.appendChild(d);
            try {
                return d
            } finally {
                d = null
            }
        },
        remove: function(b) {
            if (!a.isElement(b) || !a.isElement(b.parentNode)) return;
            try {
                a.Event.removeListener(b), a.DOM.removeCache(b)
            } catch (c) {}
            b.parentNode.removeChild(b)
        },
        insertBefore: function(b, c) {
            a.isElement(b) && a.isElement(c) && c.parentNode.insertBefore(b, c)
        },
        insertAfter: function(b, c) {
            a.isElement(b) && a.isElement(c) && (c.nextSibling ? c.parentNode.insertBefore(b, c.nextSibling) : c.parentNode.appendChild(b))
        }
    })
}(SINA),
function(a) {
    var b = a.DOM,
        c = "__Sina" + a.now(),
        d = 1,
        e = {},
        f = {
            embed: 1,
            object: 1,
            applet: 1
        };
    a.extend(b, {
        _guid: function(a) {
            var b = d.toString();
            return d++, a ? a + b : b
        },
        _expando: c,
        _noData: f,
        _cache: {},
        cache: function(d, g, h) {
            if (!d || d.nodeName && f[d.nodeName.toLowerCase()]) return;
            d == window && (d = e);
            var i = !!d.nodeType,
                j = i ? d[c] : null,
                k = i ? b._cache : d,
                l, m = typeof g == "string",
                n = typeof g == "object";
            if (i) {
                j || (d[c] = j = b._guid()), k[j] || (k[j] = {}), l = k[j];
                if (n) {
                    a.extend(l, g, !0);
                    return
                }
            } else k || (k = {}), l = k, n && a.extend(l, g, !0);
            if (m && h !== undefined) l[g] = h;
            else return m ? l[g] : l
        },
        removeCache: function(a, d) {
            if (!a || a.nodeName && f[a.nodeName.toLowerCase()]) return;
            a == window && (a = e);
            var g = !!a.nodeType,
                h = g ? a[c] : null,
                i = g ? b._cache : a,
                j, k;
            if (g && !h) return;
            j = g ? i[h] : i;
            if (d) j && delete j[d];
            else if (j)
                for (k in j) delete j[k]
        }
    })
}(SINA),
function(a) {
    var b = a.DOM,
        c = a.isElement,
        d = a.isPlainObject,
        e = a.isNumber,
        f = a.isWindow,
        g = a.DOM.style,
        h = a.UA.ie ? !0 : !1,
        i = function(a) {
            return parseInt(g(a, "left")) || 0
        },
        j = function(a) {
            return parseInt(g(a, "top")) || 0
        },
        k = function(a) {
            var b = 0;
            while (a) b += a.offsetTop, a = a.offsetParent;
            return b
        },
        l = function(a) {
            var b = 0;
            while (a) b += a.offsetLeft, a = a.offsetParent;
            return b
        };
    a.extend(b, {
        offset: function(a, b) {
            if (a && c(a)) {
                if (!(b && e(b.left) && e(b.top))) return {
                    top: l(a),
                    left: k(a)
                };
                var d = l(a),
                    f = k(a),
                    h = b.left - d,
                    m = b.top - f,
                    n = i(a),
                    o = j(a);
                g(a, "position") === "static" && g(a, "position", "relative"), g(a, "top", o + m + "px"), g(a, "left", n + h + "px")
            }
        },
        position: function(a, b) {
            if (a && c(a))
                if (b && e(b.left) && e(b.top)) g(a, "position") === "static" && g(a, "position", "relative"), g(a, "top", b.top + "px"), g(a, "left", b.left + "px");
                else return {
                    left: i(a),
                    top: j(a)
                }
        },
        scrollLeft: function(a, d) {
            if (a)
                if (c(a))
                    if (d && e(d)) a.scrollLeft = d;
                    else return a.scrollLeft;
            else if (f(a))
                if (d && e(d)) window.scrollTo(d, b.scrollTop(a));
                else return window.pageXOffset || (h ? document.documentElement.scrollLeft : document.body.scrollLeft)
        },
        scrollTop: function(a, d) {
            if (a)
                if (c(a))
                    if (d && e(d)) a.scrollTop = d;
                    else return a.scrollTop;
            else if (f(a))
                if (d && e) window.scrollTo(b.scrollLeft(a), d);
                else return window.pageYOffset || (h ? document.documentElement.scrollTop : document.body.scrollTop)
        },
        offsetParent: function(a) {
            return a && c(a) ? a.offsetParent || document.body : null
        }
    })
}(window.SINA),
function(a) {
    var b = a.DOM,
        c = a.isElement,
        d = a.isBoolean,
        e = a.isNumber,
        f = b.style,
        g = a.UA.ie ? !0 : !1;
    a.extend(b, {
        width: function(a, b) {
            if (a && c(a)) {
                if (b && e(b)) {
                    f(a, "width", b + "px");
                    return
                }
                return parseInt(a, "width") || 0
            }
            return null
        },
        height: function(a, b) {
            if (a && c(a)) {
                if (b && e(b)) {
                    f(a, "height", b + "px");
                    return
                }
                return parseInt(f(a, "height")) || 0
            }
            return null
        },
        size: function(a, c) {
            if (c) b.width(a, c.width), b.height(a, c.height);
            else {
                var d = b.width(a),
                    f = b.height(a);
                return e(d) && e(f) ? {
                    width: d,
                    height: f
                } : null
            }
        },
        innerWidth: function(a) {
            return a && c(a) ? (b.width(a) || 0) + (parseInt(f(a, "paddingLeft")) || 0) + (parseInt(f(a, "paddingRight")) || 0) : null
        },
        innerHeight: function(a) {
            return a && c(a) ? (b.height(a) || 0) + (parseInt(f(a, "paddingTop")) || 0) + (parseInt(f(a, "paddingBottom")) || 0) : null
        },
        innerSize: function(a) {
            var c = b.innerWidth(a),
                d = b.innerHeight(a);
            return e(c) && e(d) ? {
                width: c,
                height: d
            } : null
        },
        outerWidth: function(a, d) {
            return a && c(a) ? b.innerWidth(a) + (parseInt(f(a, "borderLeftWidth")) || 0) + (parseInt(f(a, "borderRightWidth")) || 0) + (d === !0) ? (parseInt(f(a, "marginLeft")) || 0) + (parseInt(f(a, "marginRight")) || 0) : 0 : null
        },
        outerHeight: function(a, d) {
            return a && c(a) ? b.innerHeight(a) + (parseInt(f(a, "borderTopWidth")) || 0) + (parseInt(f(a, "borderBottomWidth")) || 0) + (d === !0) ? (parseInt(f(a, "marginTop")) || 0) + parseInt(f(a, "marginBottom")) : 0 : null
        },
        outerSize: function(a, c) {
            var d = b.outerWidth(a, c),
                f = b.outerHeight(a, c);
            return e(d) && e(f) ? {
                width: d,
                height: f
            } : null
        },
        documentWidth: function() {
            return g ? document.documentElement.scrolllWidth : document.body.scrollWidth
        },
        documentHeight: function() {
            return g ? document.documentElement.scrollHeight : document.body.scrollHeight
        },
        viewportWidth: function() {
            return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth
        },
        viewportHeight: function() {
            return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
        }
    })
}(window.SINA),
function(a) {
    var b = a.DOM,
        c = document.addEventListener ? function(a, b, c, d) {
            a && a.addEventListener && a.addEventListener(b, c, !!d)
        } : function(a, b, c) {
            a && a.attachEvent && a.attachEvent("on" + b, c)
        },
        d = document.removeEventListener ? function(a, b, c, d) {
            a && a.removeEventListener && a.removeEventListener(b, c, !!d)
        } : function(a, b, c) {
            a && a.detachEvent && a.detachEvent("on" + b, c)
        },
        e = 1,
        f = function(a) {
            var b = document.createElement("div"),
                c;
            return a = "on" + a, c = a in b, c || (b.setAttribute(a, "return;"), c = typeof b[a] == "function"), b = null, c
        },
        g = {
            special: {},
            addListener: function(d, e, f) {
                if (!d || d.nodeType === 3 || d.nodeType === 8) return;
                a.isWindow(d) && d !== window && !d.frameElement && (d = window);
                if (!a.isFunction(f)) return;
                var h, i, j, k = !1,
                    l, m, n;
                h = b.cache(d);
                if (!h) return;
                h.__events || (h.__events = {}), i = h.__events, k = !d._customEventTarget, k && !h.__target && (h.__target = d), i[e] || (l = k && g.special[e] || {}, j = function(b, c) {
                    if (!b || !b.fixed) b = new a.EventObject(d, b, e);
                    return a.isObject(c) && a.extend(b, c), l.setup && l.setup(b), (l.handle || g._handle)(d, b, i[e].listeners)
                }, i[e] = {
                    handle: j,
                    listeners: []
                }, m = l.fix || e, n = l.capture, k && c(d, m, j, n)), i[e].listeners.push(f)
            },
            removeListener: function(c, e, f) {
                if (!c || c.nodeType === 3 || c.nodeType === 8) return;
                var h, i, j, k, l, m = !1,
                    n, o, p, q;
                h = b.cache(c);
                if (!h) return;
                i = h.__events;
                if (!i) return;
                j = i[e];
                if (j) {
                    k = j.listeners, o = k.length;
                    if (a.isFunction(f) && o) {
                        l = [], p = 0;
                        for (n = 0; n < o; n++) f !== k[n] && (l[p++] = k[n]);
                        j.listeners = l, o = l.length
                    }
                    if (f === undefined || o === 0) m = !c._customEventTarget, q = m && g.special[e] || {}, m && d(c, q.fix || e, j.handle), j = null, delete i[e]
                }
                if (e === undefined || a.isEmptyObject(i)) {
                    for (e in i) g.remove(c, e);
                    i = null, "__target" in h && (h.__target = null), delete h.__events
                }
            },
            _handle: function(a, b, c) {
                var d, e, f, g;
                c = c.slice(0);
                for (e = 0, f = c.length; e < f; ++e) g = c[e], d = g.call(a, b), d === !1 && (b.preventDefault(), b.stopPropagation());
                return d
            },
            support: f
        };
    a.Event = g, a.addListener = g.addListener, a.removeListener = g.removeListener, window.attachEvent && !window.addEventListener && g.addListener(window, "unload", function() {
        var a, c = b._cache;
        for (a in c)
            if (c[a].__target) try {
                g.removeListener(c[a].__target)
            } catch (d) {}
    })
}(SINA),
function(a) {
    var b = a.DOM,
        c = "altKey attrChange attrName bubbles button cancelable charCode clientX clientY ctrlKey currentTarget data detail eventPhase fromElement handler keyCode layerX layerY metaKey newValue offsetX offsetY originalTarget pageX pageY prevValue relatedNode relatedTarget screenX screenY shiftKey srcElement target toElement view wheelDelta which".split(" "),
        d = function(a, b, c) {
            var d = this;
            d.currentTarget = a, d.originalEvent = b || {}, b ? (d.type = b.type, d._fix()) : (d.type = c, d.target = a), d.currentTarget = a, d.fixed = !0
        };
    a.extend(d.prototype, {
        _fix: function() {
            var a = this,
                b = a.originalEvent,
                d = c.length,
                e, f = a.currentTarget,
                g = f.nodeType === 9 ? f : f.ownerDocument || document;
            while (d) e = c[--d], a[e] = b[e];
            a.target || (a.target = a.srcElement || document), a.target.nodeType === 3 && (a.target = a.target.parentNode), !a.relatedTarget && a.fromElement && (a.relatedTarget = a.fromElement === a.target ? a.toElement : a.fromElement);
            if (a.pageX === undefined && a.clientX !== undefined) {
                var h = g.documentElement,
                    i = g.body;
                a.pageX = a.clientX + (h && h.scrollLeft || i && i.scrollLeft || 0) - (h && h.clientLeft || i && i.clientLeft || 0), a.pageY = a.clientY + (h && h.scrollTop || i && i.scrollTop || 0) - (h && h.clientTop || i && i.clientTop || 0)
            }
            a.which === undefined && (a.which = a.charCode !== undefined ? a.charCode : a.keyCode), a.metaKey === undefined && (a.metaKey = a.ctrlKey), !a.which && a.button !== undefined && (a.which = a.button & 1 ? 1 : a.button & 2 ? 3 : a.button & 4 ? 2 : 0)
        },
        preventDefault: function() {
            var a = this.originalEvent;
            a.preventDefault ? a.preventDefault() : a.returnValue = !1, this.isDefaultPrevented = !0
        },
        stopPropagation: function() {
            var a = this.originalEvent;
            a.stopPropagation ? a.stopPropagation() : a.cancelBubble = !0, this.isPropagationStopped = !0
        },
        fixed: !1,
        isDefaultPrevented: !1,
        isPropagationStopped: !1
    }), a.EventObject = d
}(SINA),
function(a) {
    var b = document,
        c = a.Event,
        d = c.special,
        e = c.support,
        f = function(a, b, d) {
            var e = b.relatedTarget;
            try {
                while (e && e !== a) e = e.parentNode;
                e !== a && c._handle(a, b, d)
            } catch (f) {}
        };
    b.addEventListener && (d.mouseenter = {
        fix: "mouseover",
        setup: function(a) {
            a.type = "mouseenter"
        },
        handle: f
    }, d.mouseleave = {
        fix: "mouseout",
        setup: function(a) {
            a.type = "mouseleave"
        },
        handle: f
    }, d.focusin = {
        fix: "focus",
        capture: !0,
        setup: function(a) {
            a.type = "focusin"
        }
    }, d.focusout = {
        fix: "blur",
        capture: !0,
        setup: function(a) {
            a.type = "focusout"
        }
    })
}(SINA),
function(a) {
    var b = a.Event,
        c = a.DOM,
        d = a.isFunction,
        e = a.extend,
        f = function(a) {};
    e(f.prototype, {
        _customEventTarget: !0,
        trigger: function(a, b) {
            var e, f, g, h;
            return e = c.cache(this), e ? (f = e.__events, f ? (g = f[a], g ? (h = g.handle, d(h) && h(null, b), this) : this) : this) : this
        },
        addListener: function(a, c) {
            return b.addListener(this, a, c), this
        },
        removeListener: function(a, c) {
            return b.removeListener(this, a, c), this
        }
    }), a.EventTarget = f, a.makeTarget = function(a, b) {
        e(a.prototype, f.prototype, !0)
    }
}(SINA),
function(a) {
    var b = a.Event,
        c = a.DOM,
        d = a.extend,
        e = function(d, f, g, h) {
            if (!d || d.nodeType === 3 || d.nodeType === 8) return;
            if (g === !1 || g === !0) h = g, g = null;
            var i, j, k, l, m = f.type || f,
                n, o, p, q, r;
            h || (f = new a.EventObject(d, null, m)), i = c.cache(d), i && (j = i.__events, k = j && j[m], l = k && k.handle, a.isFunction(l) && l(f, g)), f.currentTarget = d;
            try {
                i && d["on" + m] && d["on" + m].apply(d, g) === !1 && f.preventDefault()
            } catch (s) {}
            n = d.parentNode || d.ownerDocument;
            if (!f.isPropagationStopped && n) e(n, f, g, !0);
            else if (!f.isDefaultPrevented) {
                o = f.target, p = o.nodeName.toLowerCase() === "a" && m === "click", q = b.special[m] || {};
                if ((!q._default || q._default.call(d, f) === !1) && !p && c.cache(o)) {
                    try {
                        o[m] && (r = o["on" + m], r && (o["on" + m] = null), o[m]())
                    } catch (t) {}
                    r && (o["on" + m] = r)
                }
            }
        };
    d(b, {
        trigger: e
    }), a.trigger = e
}(SINA),
function(a) {
    var b = a.Event,
        c = a.DOM,
        d = a.extend,
        e = a.isString,
        f = a.isFunction,
        g = a.isEmptyObject,
        h = a.isWindow,
        i = a.query,
        j = {
            focus: "focusin",
            blur: "focusout",
            mouseenter: "mouseover",
            mouseleave: "mouseout"
        },
        k = function(a, b, c) {
            if (a === c) return !1;
            var d = i(b, c),
                e = d.length,
                f;
            for (f = 0; f < e; f++)
                if (a === d[f]) return !0;
            return !1
        };
    d(b, {
        delegate: function(a, d, g, i) {
            if (!a || a.nodeType === 3 || a.nodeType === 8) return;
            if (!e(g) || !e(d) || !f(i)) return;
            h(a) && (a = document);
            var l, m, n, o, p, q;
            l = c.cache(a);
            if (!l) return;
            m = l.__delegate, m || (m = l.__delegate = {}), m[d] || (p = function(b) {
                var c = b.target,
                    e = m[d].delegates,
                    f = !1,
                    g, h, i, j;
                while (c !== a && !f) {
                    for (i in e)
                        if (k(c, i, a)) {
                            j = e[i], h = j.length;
                            for (g = 0; g < h; g++) j[g].call(c, b) === !1 ? (b.preventDefault(), b.stopPropagation(), f = !0) : b.isPropagationStopped && (f = !0)
                        }
                    if (f) break;
                    c = c.parentNode
                }
            }, m[d] = {
                handle: p,
                delegates: {}
            }, b.addListener(a, j[d] || d, p)), n = m[d], o = n.delegates[g], o || (o = n.delegates[g] = []), o.push(i)
        },
        detach: function(a, d, e, i) {
            if (!a || a.nodeType === 3 || a.nodeType === 8) return;
            h(a) && (a = document);
            var k, l, m, n, o, p, q, r, s, t;
            k = c.cache(a);
            if (!k) return;
            l = k.__delegate;
            if (!l) return;
            m = l[d];
            if (m) {
                n = m.delegates, o = n[e];
                if (o) {
                    q = o.length;
                    if (q && f(i)) {
                        r = [], s = 0;
                        for (p = 0; p < q; p++) t = o[p], i !== t && (r[s++] = t);
                        n[e] = r, q = r.length
                    }
                    if (i === undefined || q === 0) o = null, delete n[e]
                }
                if (e === undefined || g(n)) b.removeListener(a, j[d] || d, m.handle), n = null, m = null, delete l[d]
            }
            if (d === undefined || g(l)) {
                for (d in l) b.removeListener(a, j[d] || d, l[d].handle), delete l[d];
                l = null, delete k.__delegate
            }
        }
    }), b.undelegate = b.detach
}(SINA),
function(a) {
    var b = a.DOM,
        c = a.query,
        d = a.Array.forEach,
        e = a.Array.indexOf,
        f = a.Array.every,
        g = a.Array.some,
        h = Array.prototype,
        i = h.push,
        j = h.slice,
        k = function(a) {
            var b = a.nodeType;
            return !!(b && (b === 1 || b === 9) || a.document && a.setInterval)
        },
        l = function(a) {
            if (!(this instanceof l)) return new l(a);
            var b, d, e;
            if (typeof a == "string") this._selector = a, i.apply(this, c(a));
            else if (a && k(a)) this[0] = a, this.length = 1;
            else if (a instanceof l) i.apply(this, a.toDOMNodes());
            else if (a && (d = a.length))
                for (b = 0; b < d; b++) e = a[b], e && k(e) && i.call(this, e)
        };
    a.extend(l.prototype, {
        length: 0,
        item: function(a) {
            return new l(this[a])
        },
        toDOMNodes: function() {
            return j.call(this, 0)
        },
        each: function(a, b) {
            d(this, a, b)
        },
        find: function(a) {
            var b = [],
                d = 0,
                f = this.length,
                g, h, i, j;
            for (; d < f; d++) {
                g = this[d], g == window && (g = document), h = c(a, g), i = h.length;
                for (j = 0; j < i; j++) e(b, h[j]) === -1 && b.push(h[j])
            }
            return new l(b)
        },
        _selector: ""
    }), a.find = function(a, b) {
        return new l(c(a, b))
    }, a.NodeList = l
}(SINA),
function(a) {
    var b = a.DOM,
        c = a.Event,
        d = a.NodeList,
        e = a.Array.forEach,
        f = a.Array.indexOf,
        g = a.Array.every,
        h = a.Array.some,
        i = Array.prototype,
        j = i.push,
        k = i.slice,
        l = 1,
        m = 2,
        n = 3;
    d.$importMethod = function(b, c, g) {
        if (typeof c == "string" && c) {
            var h = b[c];
            if (h && typeof h == "function") {
                g = g || 0;
                switch (g) {
                    case 1:
                        d.prototype[c] = function() {
                            var b = k.call(arguments, 0),
                                c = 0,
                                e = this.length,
                                g, i, j, l, m = [];
                            b.unshift(null);
                            for (; c < e; c++) {
                                b[0] = this[c], g = h.apply(window, b);
                                if (g)
                                    if (a.isArray(g))
                                        for (j = 0, i = g.length; j < i; j++) l = g[j], f(m, l) === -1 && m.push(l);
                                    else f(m, g) === -1 && m.push(g)
                            }
                            return new d(m)
                        };
                        break;
                    case 2:
                        d.prototype[c] = function() {
                            var a = k.call(arguments, 0),
                                b = arguments.length === 2 || typeof arguments[0] == "object",
                                c = 0,
                                d = this.length;
                            a.unshift(null);
                            if (b)
                                for (; c < d; c++) a[0] = this[c], h.apply(window, a);
                            else if (d) return a[0] = this[0], h.apply(window, a)
                        };
                        break;
                    case 3:
                        d.prototype[c] = function() {
                            var a = k.call(arguments, 0),
                                b = arguments.length === 1,
                                c = 0,
                                d = this.length;
                            a.unshift(null);
                            if (b)
                                for (; c < d; c++) a[0] = this[c], h.apply(window, a);
                            else if (d) return a[0] = this[0], h.apply(window, a)
                        };
                        break;
                    default:
                        d.prototype[c] = function() {
                            var a = k.call(arguments, 0),
                                b = 0,
                                c = this.length,
                                d;
                            a.unshift(null);
                            for (; b < c; b++) {
                                node = this[b], a[0] = this[b], d = h.apply(window, a);
                                if (d === !0) return !0
                            }
                            if (d === !1) return !1
                        }
                }
            }
        } else a.isArray(c) && e(c, function(a) {
            d.$importMethod(b, a, g)
        })
    }, d.$importMethod(b, ["next", "previous", "parent", "children", "siblings"], l), d.$importMethod(b, ["attr", "style", "cache"], m), d.$importMethod(b, ["html", "text", "value"], n), d.$importMethod(b, ["hasClass", "addClass", "removeClass", "removeAttr", "removeCache"]), d.$importMethod(c, ["addListener", "removeListener", "trigger", "delegate", "detach", "undelegate"])
}(SINA),
function(a) {
    var b = a.DOM,
        c = a.UA,
        d = a.NodeList,
        e = a.query,
        f = a.trim,
        g = a.arrayify,
        h = a.Array.forEach,
        i = Array.prototype.slice,
        j = c.ie,
        k = c.gecko,
        l = /<(\w+)/i,
        m = /^<(\w+)\s*\/?>(?:<\/\1>)?$/,
        n = /<script([^>]*)>([\s\S]*?)<\/script>/ig,
        o = /\ssrc=(['"])(.*?)\1/i,
        p = /\scharset=(['"])(.*?)\1/i,
        q = 1,
        r = 2,
        s = 3,
        t = 4,
        u = function(a) {
            var b = a && a.nodeType;
            return !(!b || b !== 1 && b !== 3)
        },
        v = function(a) {
            var b = a.cloneNode(!0);
            return c.ie < 8 && (b.innerHTML = a.innerHTML), b
        },
        w = function(a, b, c) {
            b = b instanceof d ? b : new d(b);
            var e = null,
                f = 0,
                g = b.length,
                h;
            if (!g) return;
            e = x(a);
            switch (c) {
                case q:
                    for (; f < g - 1; f++)
                        if (h = b[f]) h.firstChild ? h.insertBefore(v(e), h.firstChild) : h.appendChild(v(e));
                    if (h = b[f]) h.firstChild ? h.insertBefore(e, h.firstChild) : h.appendChild(e);
                    break;
                case r:
                    for (; f < g - 1; f++)(h = b[f]) && h.appendChild(v(e));
                    (h = b[f]) && h.appendChild(e);
                    break;
                case s:
                    for (; f < g - 1; f++)(h = b[f]) && h.parentNode && h.parentNode.insertBefore(v(e), h);
                    (h = b[f]) && h.parentNode && h.parentNode.insertBefore(e, h);
                    break;
                case t:
                    for (; f < g - 1; f++)(h = b[f]) && h.parentNode && (h.nextSibling ? h.parentNode.insertBefore(v(e), h.nextSibling) : h.parentNode.appendChild(v(e)));
                    (h = b[f]) && h.parentNode && (h.nextSibling ? h.parentNode.insertBefore(e, h.nextSibling) : h.parentNode.appendChild(e));
                    break;
                default:
            }
        },
        x = function(a) {
            var b = null;
            return u(a) ? b = a : typeof a == "string" ? b = z(a) : a && a.length && (b = y(a)), b
        },
        y = function(a, b) {
            if (a && a.length) {
                var c = null,
                    d = 0,
                    e = a.length;
                b = b || a[0].ownerDocument, c = b.createDocumentFragment(), a.item && (a = g(a));
                for (d = 0, len = a.length; d < len; d++) c.appendChild(a[d]);
                return c
            }
            return null
        },
        z = function(b, c) {
            if (typeof b == "string" && (b = f(b))) {
                var d = null,
                    e, g, h, i = A;
                return c = c || document, e = m.exec(b), e && e[1] ? d = c.createElement(e[1]) : (e = l.exec(b), e && e[1] && (g = e[1].toLowerCase(), B[g] && a.isFunction(B[g]) && (i = B[g])), h = i(b, c).childNodes, h.length === 1 ? d = h[0].parentNode.removeChild(h[0]) : d = y(h, c)), d
            }
            return null
        },
        A = function(a, b) {
            var c = b.createElement("div");
            return c.innerHTML = a, c
        },
        B = {};
    if (k || j) {
        var C = "<table>",
            D = "</table>",
            E = /(?:\/(?:thead|tfoot|caption|col|colgroup)>)+\s*<tbody/;
        a.extend(B, {
            td: function(a, b) {
                return z("<tr>" + a + "</tr>", b)
            },
            tr: function(a, b) {
                return z("<tbody>" + a + "</tbody>", b)
            },
            tbody: function(a, b) {
                return z("<table>" + a + "</table>", b)
            },
            option: function(a, b) {
                return z("<select>" + a + "</select>", b)
            },
            col: function(a, b) {
                return z("<colgroup>" + a + "</colgroup>", b)
            },
            legend: function(a, b) {
                return z("<fieldset>" + a + "</fieldset>", b)
            }
        }), j && (B.script = function(a, b) {
            var c = b.createElement("div");
            return c.innerHTML = "-" + a, c.removeChild(c.firstChild), c
        }, j < 8 && (B.tbody = function(a, b) {
            var c = z(C + a + D, b),
                d = c.children.tags("tbody")[0];
            return c.children.length > 1 && d && !E.test(a) && tbody.parentNode.removeChild(d), c
        })), a.extend(B, {
            th: B.td,
            thead: B.tbody,
            tfoot: B.tbody,
            caption: B.tbody,
            colgroup: B.tbody,
            optgroup: B.option
        })
    }
    a.extend(d.prototype, {
        append: function(a) {
            w(a, this, r)
        },
        appendTo: function(a) {
            w(this, a, r)
        },
        addBefore: function(a) {
            w(this, a, s)
        },
        addAfter: function(a) {
            w(this, a, t)
        },
        remove: function(b) {
            h(this.find(b), function(b) {
                a.isElement(b) && b.parentNode && b.parentNode.removeChild(b)
            })
        }
    })
}(SINA),
function(a, b) {
    var c = a.isFunction,
        d = a.isObject,
        e = a.isArray,
        f = Object.prototype.toString,
        g = b.JSON,
        h = f.call(g) === "[object JSON]" && g,
        i = !!h,
        j = i,
        k = "undefined",
        l = "object",
        m = "null",
        n = "string",
        o = "number",
        p = "boolean",
        q = "date",
        r = {
            "undefined": k,
            string: n,
            "[object String]": n,
            number: o,
            "[object Number]": o,
            "boolean": p,
            "[object Boolean]": p,
            "[object Date]": q,
            "[object RegExp]": l
        },
        s = "",
        t = "{",
        u = "}",
        v = "[",
        w = "]",
        x = ",",
        y = ",\n",
        z = "\n",
        A = ":",
        B = ": ",
        C = '"',
        D = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        E = {
            "\b": "\\b",
            "\t": "\\t",
            "\n": "\\n",
            "\f": "\\f",
            "\r": "\\r",
            '"': '\\"',
            "\\": "\\\\"
        },
        F = function(a) {
            var b = typeof a;
            return r[b] || r[f.call(a)] || (b === l ? a ? l : m : k)
        },
        G = function(a) {
            return E[a] || (E[a] = "\\u" + ("0000" + (+a.charCodeAt(0)).toString(16)).slice(-4)), E[a]
        },
        H = function(a) {
            return C + a.replace(D, G) + C
        },
        I = function(a, b) {
            return a.replace(/^/gm, b)
        },
        J = function(b, g, h) {
            function G(a, b) {
                var f = a[b],
                    j = F(f),
                    C = [],
                    D = h ? B : A,
                    E, J, K, L, M;
                d(f) && c(f.toJSON) ? f = f.toJSON(b) : j === q && (f = k(f)), c(i) && (f = i.call(a, b, f)), f !== a[b] && (j = F(f));
                switch (j) {
                    case q:
                    case l:
                        break;
                    case n:
                        return H(f);
                    case o:
                        return isFinite(f) ? f + s : m;
                    case p:
                        return f + s;
                    case m:
                        return m;
                    default:
                        return undefined
                }
                for (J = r.length - 1; J >= 0; --J)
                    if (r[J] === f) throw new Error("JSON.stringify. Cyclical reference");
                E = e(f), r.push(f);
                if (E)
                    for (J = f.length - 1; J >= 0; --J) C[J] = G(f, J) || m;
                else {
                    K = g || f, J = 0;
                    for (L in K) K.hasOwnProperty(L) && (M = G(f, L), M && (C[J++] = H(L) + D + M))
                }
                return r.pop(), h && C.length ? E ? v + z + I(C.join(y), h) + z + w : t + z + I(C.join(y), h) + z + u : E ? v + C.join(x) + w : t + C.join(x) + u
            }
            if (b === undefined) return undefined;
            var i = c(g) ? g : null,
                j = f.call(h).match(/String|Number/) || [],
                k = a.JSON.dateToString,
                r = [],
                C, D, E;
            if (i || !e(g)) g = undefined;
            if (g) {
                C = {};
                for (D = 0, E = g.length; D < E; ++D) C[g[D]] = !0;
                g = C
            }
            return h = j[0] === "Number" ? (new Array(Math.min(Math.max(0, h), 10) + 1)).join(" ") : (h || s).slice(0, 10), G({
                "": b
            }, "")
        },
        K = b.eval,
        L = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        M = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,
        N = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
        O = /(?:^|:|,)(?:\s*\[)+/g,
        P = /[^\],:{}\s]/,
        Q = function(a) {
            return "\\u" + ("0000" + (+a.charCodeAt(0)).toString(16)).slice(-4)
        },
        R = function(a, b) {
            var c = function(a, d) {
                var e, f, g = a[d];
                if (g && typeof g == "object")
                    for (e in g) g.hasOwnProperty(e) && (f = c(g, e), f === undefined ? delete g[e] : g[e] = f);
                return b.call(a, d, g)
            };
            return typeof b == "function" ? c({
                "": a
            }, "") : a
        },
        S = function(a, b) {
            a = a.replace(L, Q);
            if (!P.test(a.replace(M, "@").replace(N, "]").replace(O, ""))) return R(K("(" + a + ")"), b);
            throw new SyntaxError("JSON.parse")
        },
        T;
    if (h) {
        try {
            i = "0" === h.stringify(0)
        } catch (U) {
            i = !1
        }
        try {
            j = h.parse('{"ok":false}', function(a, b) {
                return a === "ok" ? !0 : b
            }).ok
        } catch (U) {
            j = !1
        }
    }
    T = j ? h.parse : S, a.JSON = {
        stringify: i ? h.stringify : J,
        parse: function(a, b) {
            typeof a != "string" && (a += "");
            try {
                return T(a, b)
            } catch (c) {}
        },
        _stringify: J,
        _parse: S,
        useNativeParse: j,
        useNativeStringify: i
    }
}(SINA, window),
function(a) {
    var b = a.isNull,
        c = a.isUndefined,
        d = a.isFunction,
        e = a.isBoolean,
        f = a.isNumber,
        g = a.isString,
        h = a.isArray,
        i = a.isObject,
        j = Object.prototype.toString,
        k = a.Array.map,
        l = [],
        m = function(b) {
            var c = a.QueryString.unescape;
            return function d(e, f) {
                var g, h, i, j, k;
                return arguments.length !== 2 ? (e = e.split(b), d(c(e.shift()), c(e.join(b)))) : (e = e.replace(/^\s+|\s+$/g, ""), a.isString(f) && (f = f.replace(/^\s+|\s+$/g, ""), isNaN(f) || (h = +f, f === h.toString(10) && (f = h))), g = /(.*)\[([^\]]*)\]$/.exec(e), g ? (j = g[2], i = g[1], j ? (k = {}, k[j] = f, d(i, k)) : d(i, [f])) : (k = {}, e && (k[e] = f), k))
            }
        },
        n = function(a, b) {
            return a ? h(a) ? a.concat(b) : !i(a) || !i(b) ? [a].concat(b) : o(a, b) : b
        },
        o = function(a, b) {
            for (var c in b) c && b.hasOwnProperty(c) && (a[c] = n(a[c], b[c]));
            return a
        },
        p = function(a) {
            var b = a.split("&"),
                c = k(b, m("=")),
                d = {},
                e = 0,
                f = c.length;
            for (; e < f; e++) d = n(d, c[e]);
            return d
        },
        q = function(i, k, m) {
            var n, o, p, r, s, t, u = "&",
                v = "=",
                w = a.QueryString.escape;
            k = !!k;
            if (b(i) || c(i) || d(i)) return m ? w(m) + v : "";
            if (e(i) || j.call(i) === "[object Boolean]") i = +i;
            if (f(i) || g(i)) return w(m) + v + w(i);
            if (h(i)) {
                t = [], m = k ? m + "[]" : m, r = i.length;
                for (p = 0; p < r; p++) t.push(q(i[p], k, m));
                return t.join(u)
            }
            for (p = l.length - 1; p >= 0; --p)
                if (l[p] === i) return "";
            l.push(i), t = [], n = m ? m + "[" : "", o = m ? "]" : "";
            for (p in i) i.hasOwnProperty(p) && (s = n + p + o, t.push(q(i[p], k, s)));
            return l.pop(), t = t.join(u), !t && m ? m + "=" : t
        };
    a.QueryString = {
        escape: encodeURIComponent,
        unescape: function(a) {
            return decodeURIComponent(a.replace(/\+/g, " "))
        },
        stringify: q,
        parse: p
    }
}(SINA),
function(a, b, c) {
    b.IO = {
        E_START: "io-start",
        E_COMPLETE: "io-complete",
        E_SUCCESS: "io-success",
        E_FAILURE: "io-failure",
        E_CANCEL: "io-cancel",
        _splitUrl: function(a) {
            var b, c, d, e, f, g;
            return c = 0, e = a.indexOf("#", c), g = e > -1 ? a.substr(e + 1) : "", d = a.indexOf("?", c), d > -1 ? (f = e > -1 ? a.substr(d + 1, e - d - 1) : a.substr(d + 1), b = a.substr(0, d)) : (f = "", b = e > -1 ? a.substr(0, e) : a), [b, f, g]
        }
    }
}(window, SINA),
function(a, b, c) {
    var d = b.extend,
        e = b.isString,
        f = b.isObject,
        g = b.isPlainObject,
        h = b.isFunction,
        i = b.IO,
        j = b.Event,
        k = b.JSON,
        l = k && k.parse,
        m = function() {},
        n = i.E_START,
        o = i.E_COMPLETE,
        p = i.E_SUCCESS,
        q = i.E_FAILURE,
        r = i.E_CANCEL,
        s = i._splitUrl,
        t = 1,
        u = function(a, b) {
            var d, f, g = a;
            b = b.toLowerCase();
            if (!e(a)) {
                d = a.getResponseHeader("Content-Type") || "", f = b === "xml" || !b && d.indexOf("xml") > -1, g = f ? a.responseXML : a.responseText;
                if (f && (g === null || g.documentElement.nodeName === "parsererror")) throw new Error("parsererror")
            }
            if (e(g) && (b === "json" || !b && d.indexOf("json") > -1) && l) {
                g = l(g);
                if (g === c) throw new Error("parsererror")
            }
            return g
        },
        v = a.location.protocol,
        w = a.XMLHttpRequest,
        x = a.ActiveXObject,
        y = x ? function() {
            if (w && v !== "file:") try {
                return new w
            } catch (a) {}
            try {
                return new x("Microsoft.XMLHTTP")
            } catch (a) {}
        } : function() {
            return new w
        },
        z = {
            method: "GET",
            data: "",
            dataType: "text",
            async: !0,
            timeout: 0,
            cache: !1,
            headers: {},
            onstart: null,
            oncomplete: null,
            onsuccess: null,
            onfailure: null
        },
        A = function(a, b) {
            if (!(this instanceof A)) return new A(a, b);
            if (!e(a) || a.length === 0) return;
            this.url = a, this.id = t++, d(b, z), this._config = b, b.onstart && this.addListener(n, function(a) {
                b.onstart(a.xhr)
            }), b.oncomplete && this.addListener(o, function(a) {
                b.oncomplete(a.data, a.status, a.xhr)
            }), b.onsuccess && this.addListener(p, function(a) {
                b.onsuccess(a.data, a.status, a.xhr)
            }), b.onfailure && this.addListener(q, function(a) {
                b.onfailure(a.data, a.status, a.xhr)
            })
        };
    d(A.prototype, {
        _xhr: null,
        _timer: null,
        id: 0,
        running: !1,
        url: "",
        send: function(a) {
            if (this.running) return;
            this.running = !0;
            var c = this,
                e = c.url,
                f, g, h, i, j = c.running,
                k, l = c._config,
                r = l.cache,
                t = a || l.data,
                v = l.method && l.method.toUpperCase() || "GET",
                w = l.dataType && l.dataType.toLowerCase() || "text",
                x = l.async,
                z = l.headers,
                A;
            f = s(e), g = f[0], h = f[1], i = f[2], l.cache || (h += h.length ? "&sul_ts=" + b.now() : "sul_ts=" + b.now()), t && v === "GET" && (h += h.length ? "&" + t : t, t = null), t && v === "POST" && d(z, {
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
            }), e = g, h && (e += "?" + h), i && (e += "#" + i), c._xhr = k = y(), k.open(v, e, x);
            for (A in z) z.hasOwnProperty(A) && k.setRequestHeader(A, z[A]);
            k.onreadystatechange = function() {
                if (c.running && k && k.readyState === 4) {
                    var a, b = p,
                        d = "";
                    c.running = !1, k.onreadystatechange = m, c.timer && c.timer.cancel();
                    try {
                        a = k.status, a >= 200 && a < 300 || a === 304 || a === 1223 ? (b = p, d = "success") : (b = q, d = "failure"), t = u(k, w)
                    } catch (e) {
                        d = e.message, d !== "parsererror" && (b = q)
                    }
                    c.trigger(b, {
                        data: t,
                        status: d,
                        xhr: k
                    }), c.trigger(o, {
                        data: t,
                        status: d,
                        xhr: k
                    })
                } else if (!k || k.readyState === 0) c.running && (c.trigger(o, {
                    status: "aborted",
                    xhr: k
                }), c.running = !1), k && (k.onreadystatechange = m)
            }, c.trigger(n, {
                xhr: k
            });
            try {
                k.send(v === "POST" ? t : null)
            } catch (B) {
                c.trigger(q, {
                    status: "failure",
                    xhr: k
                }), c.trigger(o, {
                    status: "failure",
                    xhr: k
                })
            }
            l.timeout && (c.timer = b.later(function() {
                c.cancel(), k.abort(), k.onreadystatechange = m, c.trigger(q, {
                    status: "timeout",
                    xhr: k
                })
            }, l.timeout)), x || c.trigger(o, {
                status: "success",
                xhr: k
            })
        },
        cancel: function() {
            if (this.running) {
                this.running = !1;
                var a = this._xhr;
                a.abort(), a.onreadystatechange = m, this.timer && (this.timer.cancel(), this.timer = null), this.trigger(o, {
                    status: "aborted",
                    xhr: a
                })
            }
        }
    }), b.makeTarget(A), d(i, {
        Request: A
    })
}(window, SINA),
function(a, b, c) {
    var d = b.extend,
        e = b.isString,
        f = b.isObject,
        g = b.isPlainObject,
        h = b.isFunction,
        i = b.IO,
        j = b.Event,
        k = b.JSON,
        l = b.QueryString,
        m = function() {},
        n = i.E_START,
        o = i.E_COMPLETE,
        p = i.E_SUCCESS,
        q = i.E_FAILURE,
        r = i.E_CANCEL,
        s = i._splitUrl,
        t = /\.css(?:\?|$)/i,
        u = a.document,
        v = u.getElementsByTagName("head")[0] || u.documentElement,
        w = u.createElement("script").readyState ? function(a, b) {
            var c = a.onreadystatechange;
            a.onreadystatechange = function() {
                var d = a.readyState;
                if (d === "loaded" || d === "complete") a.onreadystatechange = null, c && c(), b.call(this)
            }
        } : function(a, b) {
            a.addEventListener("load", b, !1)
        },
        x = function(a, b) {
            var c = t.test(a),
                d = u.createElement(c ? "link" : "script");
            return c ? (d.href = a, d.rel = "stylesheet") : (d.src = a, d.async = !0), c ? h(b) && b.call(d, "") : w(d, function() {
                h(b) && b.call(d, ""), v && d.parentNode && v.removeChild(d)
            }), v.insertBefore(d, v.firstChild), d
        },
        y = function(d, f, g, i, j, k) {
            var l = s(d),
                m = l[0],
                n = l[1],
                o = l[2],
                p, q, r = null;
            return e(f) && (n += n.length ? "&" + f : f), i ? (k = k || "callback", j = j || "jsonp" + b.now() + Math.floor(Math.random() * 1e5), p = k + "=" + j, n += n.length ? "&" + p : p, q = a[j], a[j] = function(b) {
                if (h(q)) q(b);
                else {
                    a[j] = c;
                    try {
                        delete a[j]
                    } catch (d) {}
                }
                h(g) && g(b)
            }) : r = g, d = m, n && (d += "?" + n), o && (d += "#" + o), x(d, r)
        };
    d(i, {
        loadScript: function(a, b, c) {
            return h(b) && (c = b, b = null), y(a, b, c)
        },
        getJSONP: function(a, b, c) {
            var d = arguments[3],
                e = arguments[4];
            return h(b) && (e = d, d = c, c = b, b = null), y(a, b, c, !0, d, e)
        }
    })
}(window, SINA),
function(a, b, c) {
    var d = b.extend,
        e = b.IO,
        f = b.isFunction;
    d(e, {
        ajax: function(a, b) {
            var c = b && b.dataType && b.dataType.toLowerCase(),
                d = b && b.onsuccess,
                g = f(d) ? function(a) {
                    b.onsuccess({
                        data: a,
                        status: "success",
                        xhr: null
                    })
                } : null;
            switch (c) {
                case "script":
                    return e.loadScript(a, b.data, g);
                case "jsonp":
                    return e.getJSONP(a, b.data, g, b.jsonpCallback, b.jsonp);
                default:
                    var h = new e.Request(a, b);
                    return h.send(), h
            }
        },
        get: function(a, b, c, d) {
            return f(b) && (d = c, c = b, b = null), e.ajax(a, {
                method: "GET",
                data: b,
                onsuccess: c,
                dataType: d
            })
        },
        post: function(a, b, c, d) {
            return f(b) && (d = c, c = b, b = null), e.ajax(a, {
                method: "POST",
                data: b,
                onsuccess: c,
                dataType: d
            })
        }
    })
}(window, SINA)