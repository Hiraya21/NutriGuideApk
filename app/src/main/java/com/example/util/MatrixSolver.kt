package com.example.util

import kotlin.math.abs

/**
 * Solves a 3x3 system of linear equations using Cramer's Rule:
 *
 *   n1*x + n2*y + n3*z = N_req
 *   p1*x + p2*y + p3*z = P_req
 *   k1*x + k2*y + k3*z = K_req
 *
 * where x, y, z are kg of fertilizer materials required per hectare.
 */
data class MatrixSolution(
    val xKgPerHa: Double,          // kg/ha of fertilizer 1
    val yKgPerHa: Double,          // kg/ha of fertilizer 2
    val zKgPerHa: Double,          // kg/ha of fertilizer 3
    val determinantD: Double,      // Determinant D
    val determinantDx: Double,     // Determinant Dx
    val determinantDy: Double,     // Determinant Dy
    val determinantDz: Double,     // Determinant Dz
    val isUniqueSolution: Boolean, // True if D != 0
    val explanation: String
)

object MatrixSolver {

    fun solve3x3(
        // Fertilizer 1 N-P-K percentages (e.g. 14.0, 14.0, 14.0 for 14-14-14)
        n1: Double, p1: Double, k1: Double,
        // Fertilizer 2 N-P-K percentages (e.g. 16.0, 20.0, 0.0 for 16-20-0)
        n2: Double, p2: Double, k2: Double,
        // Fertilizer 3 N-P-K percentages (e.g. 46.0, 0.0, 0.0 for 46-0-0)
        n3: Double, p3: Double, k3: Double,
        // Target recommendation in kg/ha of N, P2O5, K2O (e.g. 120, 40, 30)
        nReq: Double, pReq: Double, kReq: Double
    ): MatrixSolution {

        // Convert percentage values to decimal proportions (e.g., 14% -> 0.14)
        val a11 = n1 / 100.0; val a12 = n2 / 100.0; val a13 = n3 / 100.0
        val a21 = p1 / 100.0; val a22 = p2 / 100.0; val a23 = p3 / 100.0
        val a31 = k1 / 100.0; val a32 = k2 / 100.0; val a33 = k3 / 100.0

        // Calculate Determinant D
        val D = a11 * (a22 * a33 - a23 * a32) -
                a12 * (a21 * a33 - a23 * a31) +
                a13 * (a21 * a32 - a22 * a31)

        if (abs(D) < 1e-7) {
            return MatrixSolution(
                xKgPerHa = 0.0,
                yKgPerHa = 0.0,
                zKgPerHa = 0.0,
                determinantD = D,
                determinantDx = 0.0,
                determinantDy = 0.0,
                determinantDz = 0.0,
                isUniqueSolution = false,
                explanation = "Determinant D is zero. The selected fertilizer matrix combination cannot be solved uniquely. Please check nutrient levels."
            )
        }

        // Calculate Dx (replace col 1 with targets)
        val Dx = nReq * (a22 * a33 - a23 * a32) -
                 a12 * (pReq * a33 - a23 * kReq) +
                 a13 * (pReq * a32 - a22 * kReq)

        // Calculate Dy (replace col 2 with targets)
        val Dy = a11 * (pReq * a33 - a23 * kReq) -
                 nReq * (a21 * a33 - a23 * a31) +
                 a13 * (a21 * kReq - pReq * a31)

        // Calculate Dz (replace col 3 with targets)
        val Dz = a11 * (a22 * kReq - pReq * a32) -
                 a12 * (a21 * kReq - pReq * a31) +
                 nReq * (a21 * a32 - a22 * a31)

        val x = Dx / D
        val y = Dy / D
        val z = Dz / D

        val sb = StringBuilder()
        sb.append("Cramer's Rule 3x3 Matrix Solution:\n")
        sb.append("• D = ${String.format("%.6f", D)}\n")
        sb.append("• Dx = ${String.format("%.4f", Dx)} ➔ x = Dx/D = ${String.format("%.2f", x)} kg/ha\n")
        sb.append("• Dy = ${String.format("%.4f", Dy)} ➔ y = Dy/D = ${String.format("%.2f", y)} kg/ha\n")
        sb.append("• Dz = ${String.format("%.4f", Dz)} ➔ z = Dz/D = ${String.format("%.2f", z)} kg/ha")

        return MatrixSolution(
            xKgPerHa = maxOf(0.0, x),
            yKgPerHa = maxOf(0.0, y),
            zKgPerHa = maxOf(0.0, z),
            determinantD = D,
            determinantDx = Dx,
            determinantDy = Dy,
            determinantDz = Dz,
            isUniqueSolution = true,
            explanation = sb.toString()
        )
    }
}
