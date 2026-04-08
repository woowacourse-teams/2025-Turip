package com.on.turip.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import java.util.EnumSet

class NamedFunctionTypeParameterDetector :
    Detector(),
    Detector.UastScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitMethod(node: UMethod) {
                node.uastParameters.forEach { parameter ->
                    val sourceText = parameter.sourcePsi?.text ?: return@forEach
                    val typeText = sourceText.substringAfter(':', "").trim()
                    if (typeText.isEmpty()) return@forEach

                    val functionArgsText = extractFunctionArgumentList(typeText) ?: return@forEach
                    val hasUnnamedFunctionArg =
                        splitTopLevel(functionArgsText, ',')
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .any { !containsTopLevelColon(it) }

                    if (hasUnnamedFunctionArg) {
                        context.report(
                            issue = ISSUE,
                            scope = parameter as UElement,
                            location = context.getLocation(parameter as UElement),
                            message = "Name function type arguments explicitly. Use '(arg: Type) -> Return' instead of '(Type) -> Return'.",
                        )
                    }
                }
            }
        }

    private fun extractFunctionArgumentList(typeText: String): String? {
        val trimmed = typeText.removePrefix("suspend ").trim()
        val start = trimmed.indexOf('(')
        if (start < 0) return null

        val end = findMatchingParen(trimmed, start) ?: return null
        val arrowIndex = trimmed.indexOf("->", end + 1)
        if (arrowIndex < 0) return null

        return trimmed.substring(start + 1, end)
    }

    private fun findMatchingParen(
        text: String,
        openIndex: Int,
    ): Int? {
        var depth = 0
        for (i in openIndex until text.length) {
            when (text[i]) {
                '(' -> {
                    depth++
                }

                ')' -> {
                    depth--
                    if (depth == 0) return i
                }
            }
        }
        return null
    }

    private fun splitTopLevel(
        text: String,
        delimiter: Char,
    ): List<String> {
        val result = mutableListOf<String>()
        var parenDepth = 0
        var angleDepth = 0
        var bracketDepth = 0
        var last = 0

        text.forEachIndexed { index, ch ->
            when (ch) {
                '(' -> {
                    parenDepth++
                }

                ')' -> {
                    if (parenDepth > 0) parenDepth--
                }

                '<' -> {
                    angleDepth++
                }

                '>' -> {
                    if (angleDepth > 0) angleDepth--
                }

                '[' -> {
                    bracketDepth++
                }

                ']' -> {
                    if (bracketDepth > 0) bracketDepth--
                }

                delimiter -> {
                    if (parenDepth == 0 && angleDepth == 0 && bracketDepth == 0) {
                        result.add(text.substring(last, index))
                        last = index + 1
                    }
                }
            }
        }

        result.add(text.substring(last))
        return result
    }

    private fun containsTopLevelColon(text: String): Boolean {
        var parenDepth = 0
        var angleDepth = 0
        var bracketDepth = 0

        text.forEach { ch ->
            when (ch) {
                '(' -> parenDepth++
                ')' -> if (parenDepth > 0) parenDepth--
                '<' -> angleDepth++
                '>' -> if (angleDepth > 0) angleDepth--
                '[' -> bracketDepth++
                ']' -> if (bracketDepth > 0) bracketDepth--
                ':' -> if (parenDepth == 0 && angleDepth == 0 && bracketDepth == 0) return true
            }
        }
        return false
    }

    companion object {
        val ISSUE: Issue =
            Issue.create(
                id = "UnnamedFunctionTypeArgument",
                briefDescription = "Function type argument name is missing",
                explanation =
                    """
                    Function type parameters in method signatures should name each argument for readability.
                    For example, prefer '(contentId: Long) -> Unit' over '(Long) -> Unit'.
                    """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 6,
                severity = Severity.WARNING,
                implementation =
                    Implementation(
                        NamedFunctionTypeParameterDetector::class.java,
                        EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES),
                    ),
            )
    }
}
