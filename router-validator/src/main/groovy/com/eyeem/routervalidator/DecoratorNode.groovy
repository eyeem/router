package com.eyeem.routervalidator

class DecoratorNode {
    String className
    String variableName

    DecoratorNode(String className) {
        this.className = className;
        this.variableName = className.replaceAll(~/\./, "_").replaceAll("\\\$", "_")
    }
}