class RouterNode {
    String path;
    String type;
    ArrayList<String> usedDecoratorClasses;
    ArrayList<String> usedHolderClasses;
    ArrayList<String> usedResources;

    RouterNode(String path, Map values) {
        this.path = path
        this.type = values.type
    }
}