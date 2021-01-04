package com.adamratzman.utils

enum class UikitName(val cssClassOrAttributeName: String) {
    // MARGIN
    MarginSmallLeft("uk-margin-small-left"),
    MarginMediumLeft("uk-margin-medium-left"),
    MarginLargeLeft("uk-margin-large-left"),
    MarginXlargeLeft("uk-margin-xlarge-left"),
    MarginSmallBottom("uk-margin-small-bottom"),
    MarginMediumBottom("uk-margin-medium-bottom"),
    MarginLargeBottom("uk-margin-large-bottom"),
    MarginXLargeBottom("uk-margin-xlarge-bottom"),
    MarginSmallRight("uk-margin-small-right"),
    MarginMediumRight("uk-margin-medium-right"),
    MarginLargeRight("uk-margin-large-right"),
    MarginXlargeRight("uk-margin-xlarge-right"),
    MarginSmallTop("uk-margin-small-top"),
    MarginMediumTop("uk-margin-medium-top"),
    MarginLargeTop("uk-margin-large-top"),
    MarginXLargeTop("uk-margin-xlarge-top"),
    MarginAutoVertical("uk-margin-auto-vertical"),
    MarginRemoveLeft("uk-margin-remove-left"),
    MarginRemoveRight("uk-margin-remove-right"),
    MarginRemoveTop("uk-margin-remove-top"),
    MarginRemoveBottom("uk-margin-remove-bottom"),
    MarginRemoveVertical("uk-margin-remove-vertical"),
    MarginAuto("uk-margin-auto"),
    UkMargin("uk-margin"),

    // Padding
    PaddingRemoveLeft("uk-padding-remove-left"),
    PaddingRemoveRight("uk-padding-remove-right"),
    PaddingRemoveTop("uk-padding-remove-top"),
    PaddingRemoveBottom("uk-padding-remove-bottom"),
    PaddingRemoveHorizontal("uk-padding-remove-horizontal"),
    PaddingRemoveVertical("uk-padding-remove-vertical"),

    PaddingSmall("uk-padding-small"),
    PaddingMedium("uk-padding-medium"),
    PaddingLarge("uk-padding-large"),


    // Width
    WidthOneOne("uk-width-1-1"),
    WidthOneHalf("uk-width-1-2"),
    WidthOneThird("uk-width-1-3"),
    WidthOneFourth("uk-width-1-4"),
    WidthThreeFourths("uk-width-3-4"),
    WidthTwoThirds("uk-width-2-3"),
    WidthAuto("uk-width-auto"),
    WidthExpand("uk-width-expand"),

    // Height
    HeightMedium("uk-height-medium"),

    // Align
    UkAlignCenter("uk-align-center"),

    // NAVBAR
    NavbarContainer("uk-navbar-container"),
    NavbarTransparent("uk-navbar-transparent"),
    NavbarLeft("uk-navbar-left"),
    NavbarRight("uk-navbar-right"),
    NavbarNav("uk-navbar-nav"),
    NavbarItem("uk-navbar-item"),
    NavbarToggle("uk-navbar-toggle"),
    NavbarToggleIcon("uk-navbar-toggle-icon"),

    // Nav
    UkNav("uk-nav"),
    UkNavPrimary("uk-nav-primary"),
    UkNavCenter("uk-nav-center"),
    UkNavDivider("uk-nav-divider"),

    // Section
    UkSection("uk-section"),
    UkSectionLarge("uk-section-large"),


    // ICON
    Icon("uk-icon"),

    // HIDDEN
    HiddenLarge("uk-hidden@l"),
    HiddenMedium("uk-hidden@m"),
    HiddenSmall("uk-hidden@s"),

    // Visible
    VisibleLarge("uk-visible@l"),
    VisibleSmall("uk-visible@s"),
    VisibleMedium("uk-visible@m"),
    // TOGGLE

    // Canvas
    OffCanvasBar("uk-offcanvas-bar"),
    OffCanvasClose("uk-offcanvas-close"),

    // Flex
    Flex("uk-flex"),
    FlexColumn("uk-flex-column"),

    // Float
    FloatLeft("uk-float-left"),
    FloatRight("uk-float-right"),

    // Attributes
    ToggleAttribute("uk-toggle"),
    OffCanvasAttribute("uk-offcanvas"),
    NavbarAttribute("uk-navbar"),
    NavbarToggleIconAttribute("uk-navbar-toggle-icon"),
    CloseAttribute("uk-close"),
    IconAttribute("uk-icon"),
    UkTooltipAttribute("uk-tooltip"),
    UkGridAttribute("uk-grid"),
    UkSpinnerAttribute("uk-spinner"),

    // Text
    TextCenter("uk-text-center"),

    // Miscellaneous
    Active("uk-active"),
    UkInline("uk-inline"),

    // Input
    UkInput("uk-input"),
    UkSelect("uk-select"),
    UkRange("uk-range"),

    // UK headers
    UkH1("uk-h1"),
    UkH2("uk-h2"),
    UkH3("uk-h3"),
    UkH4("uk-h4"),
    UkH5("uk-h5"),
    UkH6("uk-h6"),

    // Grid
    GridCollapse("uk-grid-collapse"),
    GridSmall("uk-grid-small"),

    // Child width
    ChildWidthExpand("uk-child-width-expand"),
    ChildWidthOneThird("uk-child-width-1-3"),
    ChildWidthOneHalf("uk-child-width-1-2"),

    // Card
    UkCard("uk-card"),
    UkCardDefault("uk-card-default"),
    UkCardBody("uk-card-body"),
    UkCardTitle("uk-card-title"),

    // Form
    UkFormIcon("uk-form-icon"),


    ;

    override fun toString(): String = cssClassOrAttributeName

    val asString get() = toString()
    val small get() = "$asString@s"
    val medium get() = "$asString@m"
    val large get() = "$asString@l"
}