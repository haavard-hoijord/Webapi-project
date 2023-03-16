package app.web.api;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Validator<T> {
	private final Function<T, Boolean> check;
	private List<Validator<T>> children;

	@SafeVarargs
	public Validator(Function<T, Boolean> check, Validator<T>... children) {
		this.check = check;
		this.children = List.of(children);
	}

	public Validator(Function<T, Boolean> check) {
		this.check = check;
	}

	@SafeVarargs
	public static <T> Validator<T> of(Function<T, Boolean>... checks) {
		Validator<T> validator = null;
		Validator<T> lastValidator = null;

		for (Function<T, Boolean> check : checks) {
			Validator<T> validator1 = new Validator<>(check);

			if (validator == null) {
				validator = validator1;
			} else {
				lastValidator.children = List.of(validator1);
			}

			lastValidator = validator1;
		}

		return validator;
	}

	public boolean run(T ob) {
		return check.apply(ob) && (children == null || children.stream().filter(Objects::nonNull).allMatch(s -> s.run(ob)));
	}
}
