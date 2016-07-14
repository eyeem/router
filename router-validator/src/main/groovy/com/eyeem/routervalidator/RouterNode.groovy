package com.eyeem.routervalidator

class RouterNode {

    // input values
    String decoratorsPackageName
    String holdersPackageName
    String path;
    String type;
    Map values;

    // parsed values
    ArrayList<String> decorators = new ArrayList<>();
    ArrayList<String> holders = new ArrayList<>();
    ArrayList<String> otherClasses = new ArrayList<>();
    ArrayList<String> resources = new ArrayList<>();

    RouterNode parse() {

        values?.holders?.forEach {
            item -> holders.add(classify(holdersPackageName, detuple(item)))
        }

        values?.decorators?.forEach {
            item -> decorators.add(classify(decoratorsPackageName, detuple(item)))
        }

        otherClasses.add detuple(values?.request?.pagination)
        otherClasses.add detuple(values?.request?.declutter)
        otherClasses = otherClasses.findAll{ it != null }.collect{ classify(null, it) + ".class"}

        scanForResources(resources, values)

        return this
    }

    static String detuple(Object object) {
        if (object instanceof String) return (String)object;
        if (object instanceof Map) {
            Map map = object;
            Map.Entry e = map.entrySet().iterator().next();
            return e.getKey().toString();
        }
        return null;
    }

    static String classify(String packageName, String className) {
        if (packageName != null && className.startsWith(".")) {
            return packageName + className.replaceAll("\\\$", ".");
        } else if (className.contains(".")) {
            return className.replaceAll("\\\$", ".");
        } else {
            return packageName + "." + className;
        }
    }

    String decorators() {
        flatten(decorators);
    }

    String holders() {
        flatten(holders);
    }

    String resources() {
        resources.join(", ")
    }

    String otherClasses() {
        otherClasses.join(", ")
    }

    String flatten(ArrayList<String> items) {
        items.collect{it + ".class"}.join(", ")
    }

    String path() {
        // public static final String PATH_${node.type.toUpperCase()} = "${node.path}";

        String routerUrl = cleanUrl(path);
        String[] routerParts = routerUrl.split("/");

        PathMethod method = urlToPathMethod(routerParts)
        method.type = type;
        method.print();
    }

    static String cleanUrl(String url) {
        if (url.startsWith("/")) {
            return url.substring(1, url.length());
        }
        return url;
    }

    static class PathMethod {
        String type
        ArrayList<String> segments = new ArrayList<>()
        ArrayList<String> params = new ArrayList<>()

        String print() {
            boolean isConstant = params.size() == 0;

            StringBuilder sb = new StringBuilder(isConstant ? "public final static" : "public static")
                .append(" String PATH_")
                .append(type.toUpperCase())
            if (isConstant) { // simple path
                sb.append(" = \"" + path() + "\";")
            } else {
                sb
                    .append("(").append(params.collect{"String " +it}.join(", ")).append(") { ")
                    .append("return String.format(Locale.US, ")
                    .append("\"").append(path()).append("\"").append(", ")
                    .append(params.join(", "))
                    .append(");")
                    .append(" }")

            }
        }

        String path() {
            segments.join("/")
        }
    }

    static PathMethod urlToPathMethod(String[] routerParts) {
        PathMethod pathMethod = new PathMethod();
        for (int index = 0; index < routerParts.length; index++) {
            String routerPart = routerParts[index];

            if (routerPart.length() > 0 && routerPart.charAt(0) == ':') {
                String key = routerPart.substring(1, routerPart.length());

                // handle :whatever: param
                if (key.charAt(key.length()-1) == ':') {
                    key = key.substring(0, key.length()-1);
                }

                pathMethod.params.add(key);
                pathMethod.segments.add("%" + (pathMethod.params.size()) + "\$s")
            } else {
                pathMethod.segments.add(routerPart)
            }
        }

        return pathMethod;
    }

    static void scanForResources(ArrayList<String> resources, Object object) {
        if (object instanceof ArrayList) {
            ArrayList array = object
            array.forEach {
                item -> scanForResources(resources, item)
            }
        }
        else if (object instanceof Map) {
            Map map = object;
            map.forEach {
                key, value -> scanForResources(resources, value)
            }
        }
        else if (object instanceof String) {
            String value = object
            if (value.startsWith("R.")) {
                resources.add(value);
            }
        }
    }
}