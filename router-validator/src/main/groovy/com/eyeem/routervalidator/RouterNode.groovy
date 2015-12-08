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
            return packageName + className;
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