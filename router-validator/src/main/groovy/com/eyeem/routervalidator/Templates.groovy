package com.eyeem.routervalidator

class Templates {

    static final String SIMPLE = '''\
package $packageName;

public final class $className {

<% access.paths.eachWithIndex { item, index -> %>\\
    public static final String PATH_$index = "$item";
<% } %>\\

}
'''

}
