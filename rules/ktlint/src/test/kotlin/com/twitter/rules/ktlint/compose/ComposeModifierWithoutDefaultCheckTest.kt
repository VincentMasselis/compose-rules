// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeModifierWithoutDefaultCheckTest {

    private val modifierRuleAssertThat = assertThatRule { ComposeModifierWithoutDefaultCheck() }

    @Test
    fun `errors when a Composable has modifiers but without default values, and is able to auto fix it`() {
        @Language("kotlin")
        val composableCode = """
                @Composable
                fun Something(modifier: Modifier) { }
                @Composable
                fun Something(modifier: Modifier = Modifier, modifier2: Modifier) { }
        """.trimIndent()

        modifierRuleAssertThat(composableCode)
            .hasLintViolations(
                LintViolation(
                    line = 2,
                    col = 15,
                    detail = ComposeModifierWithoutDefaultCheck.MissingModifierDefaultParam
                ),
                LintViolation(
                    line = 4,
                    col = 46,
                    detail = ComposeModifierWithoutDefaultCheck.MissingModifierDefaultParam
                )
            )
            .isFormattedAs(
                """
                @Composable
                fun Something(modifier: Modifier = Modifier) { }
                @Composable
                fun Something(modifier: Modifier = Modifier, modifier2: Modifier = Modifier) { }
                """.trimIndent()
            )
    }

    @Test
    fun `passes when a Composable has modifiers with defaults`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier = Modifier) {
                    Row(modifier = modifier) {
                    }
                }
                @Composable
                fun Something(modifier: Modifier = Modifier.fillMaxSize()) {
                    Row(modifier = modifier) {
                    }
                }
                @Composable
                fun Something(modifier: Modifier = SomeOtherValueFromSomeConstant) {
                    Row(modifier = modifier) {
                    }
                }
            """.trimIndent()
        modifierRuleAssertThat(code).hasNoLintViolations()
    }
}
