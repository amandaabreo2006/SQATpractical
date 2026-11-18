package com.sqat.orangehrm.data;

import com.sqat.orangehrm.config.ConfigReader;
import com.sqat.orangehrm.constants.Messages;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

/**
 * {@code @MethodSource} factories. Each method turns a BVA range from the test-design document
 * into JUnit arguments: (boundary label, length, input value, expected error message or "").
 */
public final class TestDataProvider {

    public static final int NAME_MIN = 1;
    public static final int NAME_MAX = 30;
    public static final int USERNAME_MIN = 5;
    public static final int USERNAME_MAX = 40;

    private TestDataProvider() {
    }

    /** BVA-01 / BVA-02: Employee First Name and Last Name, range [1, 30]. Below min means empty, i.e. "Required". */
    public static Stream<Arguments> employeeNameBoundaries() {
        return BoundaryValues.sevenPoint(NAME_MIN, NAME_MAX).stream().map(p -> Arguments.of(
                p.label(),
                p.value(),
                StringGenerator.name(p.value()),
                p.valid() ? "" : (p.value() < NAME_MIN ? Messages.REQUIRED : Messages.shouldNotExceed(NAME_MAX))));
    }

    /** BVA-03: Admin user Username, range [5, 40]. */
    public static Stream<Arguments> usernameBoundaries() {
        return BoundaryValues.sevenPoint(USERNAME_MIN, USERNAME_MAX).stream().map(p -> Arguments.of(
                p.label(),
                p.value(),
                StringGenerator.username(p.value()),
                p.valid() ? ""
                        : (p.value() < USERNAME_MIN ? Messages.shouldBeAtLeast(USERNAME_MIN)
                        : Messages.shouldNotExceed(USERNAME_MAX))));
    }

    /** BVA-04: Admin user Password, range [8, 64] (min from the server-side password policy). */
    public static Stream<Arguments> passwordBoundaries() {
        int min = ConfigReader.getInt("password.min.length");
        int max = ConfigReader.getInt("password.max.length");
        return BoundaryValues.sevenPoint(min, max).stream().map(p -> Arguments.of(
                p.label(),
                p.value(),
                StringGenerator.password(p.value()),
                p.valid() ? ""
                        : (p.value() < min ? Messages.passwordMinLength(min) : Messages.shouldNotExceed(max))));
    }
}
