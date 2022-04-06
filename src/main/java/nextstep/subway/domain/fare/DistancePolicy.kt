package nextstep.subway.domain.fare

import nextstep.subway.domain.fare.FarePolicy
import nextstep.subway.domain.fare.Fare
import nextstep.subway.domain.fare.DistancePolicy
import java.util.Arrays
import java.util.function.Predicate
import kotlin.math.ceil

class DistancePolicy private constructor(private val distance: Int) : FarePolicy {
    override fun calculate(fare: Fare) {
        fare.add(Policy.getOverFare(distance))
    }

    private enum class Policy(private val predicate: Predicate<Int>, private val criteria: Int) {
        STANDARD(
            Predicate { distance: Int -> distance <= 10 },
            0
        ),
        NORMAL(
            Predicate { distance: Int -> distance in 11..50 },
            5
        ),
        LONGEST(
            Predicate { distance: Int -> distance > 50 },
            8
        );

        private fun match(distance: Int): Boolean {
            return predicate.test(distance)
        }

        companion object {
            private const val NOT_EXISTS_OVER_FARE = 0

            fun getOverFare(distance: Int): Int {
                val policy = findType(distance)
                return if (isStandard(distance)) NOT_EXISTS_OVER_FARE else calculateOverFare(distance, policy.criteria)
            }

            private fun isStandard(distance: Int): Boolean {
                return findType(distance) == STANDARD
            }

            private fun calculateOverFare(distance: Int, criteria: Int): Int {
                return ((ceil(((distance - 11) / criteria).toDouble()) + 1) * 100).toInt()
            }

            private fun findType(distance: Int): Policy {
                return Arrays.stream(values())
                    .filter { policy: Policy -> policy.match(distance) }
                    .findFirst()
                    .orElse(STANDARD)
            }
        }
    }

    companion object {
        @JvmStatic
        fun from(distance: Int): DistancePolicy {
            return DistancePolicy(distance)
        }
    }
}