JAVAC = javac
JAVA = java

SRC_DIRS = . utils menu db controls
BIN_DIR = bin

MAIN_CLASS = Main
JAR = mssql.jar

# Collect all Java files
SOURCES = $(foreach dir, $(SRC_DIRS), $(wildcard $(dir)/*.java))
CLASSES = $(SOURCES:%.java=$(BIN_DIR)/%.class)

# Compile each .java file
$(BIN_DIR)/%.class: %.java
	$(JAVAC) -d $(BIN_DIR) $<

# Build all classes
build: $(CLASSES)

# Run the program
# changed ; to :
run: build
	$(JAVA) -cp "$(BIN_DIR):$(JAR)" $(MAIN_CLASS) 

# Clean compiled classes
clean:
	rm -rf $(BIN_DIR)/*
