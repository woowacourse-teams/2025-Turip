package com.on.turip.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class TuripIssueRegistry : IssueRegistry() {
    override val issues: List<Issue> =
        listOf(
            NamedFunctionTypeParameterDetector.ISSUE,
        )

    override val api: Int = CURRENT_API

    override val vendor: Vendor =
        Vendor(
            vendorName = "Turip",
            feedbackUrl = "https://github.com/your-org/your-repo/issues",
            contact = "dev@turip.app",
        )
}
