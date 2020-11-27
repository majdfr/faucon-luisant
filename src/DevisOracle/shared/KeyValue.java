package DevisOracle.shared;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KeyValue {

    private final IntegerProperty key;
    private final StringProperty value;


    public int getKey() {
        return key.get();
    }

    public IntegerProperty keyProperty() {
        return key;
    }

    public void setKey(int key) {
        this.key.set(key);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public KeyValue(Integer key, String value) {
        super();
        this.key = new SimpleIntegerProperty(key);
        this.value = new SimpleStringProperty(value);
    }

    public KeyValue() {
        super();
        this.key = new SimpleIntegerProperty(0);
        this.value = new SimpleStringProperty("");
    }


    @Override
    public String toString() {
        return "KeyValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
