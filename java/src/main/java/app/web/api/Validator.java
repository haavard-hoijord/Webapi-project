package app.web.api;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Validator<T> {
    public Function<T, Boolean> check;
    public List<Validator<T>> children;

    @SafeVarargs
    public Validator(Function<T, Boolean> check, Validator<T>... children) {
        this.check = check;
        this.children = List.of(children);
    }

    public Validator(Function<T, Boolean> check) {
        this.check = check;
    }

    public boolean run(T ob){
        return check.apply(ob) && (children == null || children.stream().filter(Objects::nonNull).allMatch(s -> s.run(ob)));
    }
}
