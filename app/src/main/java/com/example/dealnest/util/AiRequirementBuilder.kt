package com.example.dealnest.util

import com.example.dealnest.model.AiClientRequirementBrief
import java.util.Locale

object AiRequirementBuilder {

    fun buildFromPrompt(rawPrompt: String): AiClientRequirementBrief {
        val p = rawPrompt.lowercase(Locale.ROOT)

        val websiteType: String
        val category: String
        val suggestedPages: List<String>
        val suggestedFeatures: List<String>
        val requiredSkills: List<String>
        val complexity: String
        val questions: List<String>
        val deadlineDays: Int
        val budgetMin: Double
        val budgetMax: Double
        val title: String

        when {
            p.contains("clothing") || p.contains("shop") || p.contains("store") || p.contains("ecommerce") || p.contains("e-commerce") || p.contains("fashion") -> {
                websiteType = "E-Commerce"
                category = "Fashion & Apparel E-Commerce"
                title = if (rawPrompt.length > 8 && !rawPrompt.startsWith("i need", ignoreCase = true)) {
                    "Custom Online Store: ${rawPrompt.take(40).trim()}"
                } else {
                    "Modern E-Commerce Store for Fashion & Apparel"
                }
                suggestedPages = listOf(
                    "Home / Hero Lookbook",
                    "Product Collection & Filtering",
                    "Product Detail Page (Sizes/Colors)",
                    "Cart & Seamless Checkout",
                    "Brand Story / About Us",
                    "Shipping, Returns & FAQ"
                )
                suggestedFeatures = listOf(
                    "Mobile-First Responsive Layout",
                    "Product Variant Selectors & Size Guide",
                    "Stripe / Shopify Payment Gateways",
                    "Customer Review & Rating System",
                    "Instagram Feed & Social Proof",
                    "Automated Abandoned Cart Email Integration"
                )
                requiredSkills = listOf("Shopify", "Liquid", "Figma", "Tailwind CSS", "JavaScript")
                complexity = "Moderate to High"
                questions = listOf(
                    "How many initial product SKUs will you launch with?",
                    "Do you have existing photography and brand guidelines ready?",
                    "Do you require inventory synchronization with a physical POS system?"
                )
                deadlineDays = 14
                budgetMin = 1500.0
                budgetMax = 3500.0
            }

            p.contains("saas") || p.contains("software") || p.contains("app") || p.contains("tech") || p.contains("ai") || p.contains("startup") -> {
                websiteType = "SaaS"
                category = "Technology & SaaS Platform"
                title = "High-Converting SaaS Landing Page & Web Platform"
                suggestedPages = listOf(
                    "Hero Landing Page with Interactive Demo",
                    "Feature Deep-Dive & Architecture",
                    "Transparent Pricing Calculator",
                    "Customer Case Studies & Testimonials",
                    "Interactive Documentation / Knowledge Base",
                    "Contact & Sales Demo Booking"
                )
                suggestedFeatures = listOf(
                    "Interactive Feature Tabs & Micro-Animations",
                    "Interactive Tiered Pricing Switcher (Monthly/Annual)",
                    "Single Sign-On (SSO) Portal Integration",
                    "SEO Optimized Technical Blog (MDX/CMS)",
                    "Sub-second Core Web Vitals Optimization",
                    "Live Chat Widget & Analytics Tracking"
                )
                requiredSkills = listOf("Next.js", "React", "TypeScript", "Tailwind CSS", "Framer Motion", "Figma")
                complexity = "High"
                questions = listOf(
                    "Do you need a headless CMS for self-managed blogs and documentation?",
                    "Will users sign up and pay directly through this marketing site?",
                    "Do you have an existing design system in Figma?"
                )
                deadlineDays = 18
                budgetMin = 2200.0
                budgetMax = 4800.0
            }

            p.contains("portfolio") || p.contains("agency") || p.contains("photographer") || p.contains("artist") || p.contains("architect") -> {
                websiteType = "Portfolio"
                category = "Creative Showcase & Agency"
                title = "Award-Winning Creative Portfolio & Case Study Showcase"
                suggestedPages = listOf(
                    "Minimalist Visual Hero Gallery",
                    "In-Depth Case Study Template",
                    "Services & Process Workflow",
                    "About Me / Press & Exhibitions",
                    "Interactive Project Inquiry Form"
                )
                suggestedFeatures = listOf(
                    "Fluid Page Transitions & Custom Cursor",
                    "Dynamic Video & High-Res Image Lightboxes",
                    "CMS for Drag-and-Drop Project Updates",
                    "Responsive Breakpoints for iPad & Mobile",
                    "Dark / Light Mode Dynamic Switcher"
                )
                requiredSkills = listOf("Webflow", "Framer", "Figma", "Interaction Design", "CSS3 Animations")
                complexity = "Moderate"
                questions = listOf(
                    "How many case studies do you plan to showcase initially?",
                    "Do you prefer Webflow or Framer for managing your ongoing updates?",
                    "Do you have high-resolution photography ready for each project?"
                )
                deadlineDays = 10
                budgetMin = 1000.0
                budgetMax = 2500.0
            }

            p.contains("clinic") || p.contains("doctor") || p.contains("dental") || p.contains("salon") || p.contains("booking") || p.contains("appointment") -> {
                websiteType = "Booking"
                category = "Healthcare & Service Appointment Booking"
                title = "Professional Practice Website with Online Patient Booking"
                suggestedPages = listOf(
                    "Trust-Building Homepage with Doctor Bios",
                    "Services & Transparent Pricing",
                    "Real-Time Online Appointment Calendar",
                    "Patient Reviews & Verified Testimonials",
                    "Clinic Location, Hours & Map Directions",
                    "Pre-Visit Intake Forms"
                )
                suggestedFeatures = listOf(
                    "Calendly / Acuity Online Booking Embed",
                    "HIPAA-Compliant Contact Inquiries",
                    "Automated SMS & Email Appointment Reminders",
                    "Interactive Google Map Integration",
                    "Emergency Call & WhatsApp Quick-Dial Buttons"
                )
                requiredSkills = listOf("WordPress", "Elementor", "Figma", "PHP", "SEO")
                complexity = "Moderate"
                questions = listOf(
                    "Which scheduling/booking platform does your practice currently use?",
                    "Do you need multi-location clinic routing?",
                    "Will patients pay consultation fees online before appointment?"
                )
                deadlineDays = 12
                budgetMin = 1200.0
                budgetMax = 2800.0
            }

            p.contains("restaurant") || p.contains("cafe") || p.contains("food") || p.contains("bakery") -> {
                websiteType = "Landing Page"
                category = "Food & Hospitality"
                title = "Appetizing Restaurant Website with Online Menu & Reservations"
                suggestedPages = listOf(
                    "Atmospheric Homepage with Video Reel",
                    "Categorized Food & Drink Menu (with dietary tags)",
                    "Table Reservation System (OpenTable/SevenRooms)",
                    "Private Events & Catering Inquiry",
                    "Location, Parking & Opening Hours"
                )
                suggestedFeatures = listOf(
                    "Interactive Mobile Menu with Vegan/GF Filters",
                    "Direct Online Ordering Integration (DoorDash/UberEats/ChowNow)",
                    "Table Reservation Widget",
                    "High-Resolution Visual Food Gallery"
                )
                requiredSkills = listOf("WordPress", "Webflow", "Figma", "Responsive HTML5")
                complexity = "Low to Moderate"
                questions = listOf(
                    "Do you need to take online pickup orders directly from your site?",
                    "Are menu items and prices updated frequently by staff?",
                    "Do you have high-quality photos of signature dishes?"
                )
                deadlineDays = 8
                budgetMin = 900.0
                budgetMax = 2000.0
            }

            else -> {
                websiteType = "Corporate"
                category = "Business & Professional Services"
                title = if (rawPrompt.isNotBlank()) "Custom Business Website: ${rawPrompt.take(35).trim()}" else "Professional Modern Business Website"
                suggestedPages = listOf(
                    "Executive Homepage with Clear Value Proposition",
                    "Services / Core Solutions Overview",
                    "About the Team & Leadership",
                    "Client Testimonials & Verified Case Studies",
                    "Industry Insights & News",
                    "Lead Capture & Discovery Call Contact"
                )
                suggestedFeatures = listOf(
                    "High-Converting Lead Generation Forms",
                    "Speed & Mobile Performance Optimization",
                    "SEO Meta Tags & Schema.org Local Business Markup",
                    "Interactive FAQ Accordion",
                    "CRM Integration (HubSpot / Mailchimp)"
                )
                requiredSkills = listOf("Webflow", "Next.js", "Figma", "Tailwind CSS")
                complexity = "Moderate"
                questions = listOf(
                    "What is the single most important action visitors should take on your site?",
                    "Do you have established brand colors, logos, and copy ready?",
                    "What target deadline are you aiming for launch?"
                )
                deadlineDays = 14
                budgetMin = 1400.0
                budgetMax = 3200.0
            }
        }

        val description = buildString {
            append("We are seeking a talented and detail-oriented web designer to build a high-performance $websiteType website for our $category.\n\n")
            append("Key Project Objectives:\n")
            append("• Deliver a clean, professional aesthetic tailored to our target audience.\n")
            append("• Optimize page structure across ${suggestedPages.size} primary views: ${suggestedPages.joinToString(", ")}.\n")
            append("• Implement core capabilities including: ${suggestedFeatures.take(3).joinToString(", ")}.\n")
            append("• Ensure flawless mobile responsiveness and high Core Web Vitals performance.\n\n")
            append("Design & Tech Preferences: ${requiredSkills.joinToString(", ")}.\n")
            append("Estimated timeline: $deadlineDays days. We look forward to reviewing tailored proposals and starting immediately.")
        }

        return AiClientRequirementBrief(
            projectTitle = title,
            websiteType = websiteType,
            businessCategory = category,
            suggestedPages = suggestedPages,
            pageCount = suggestedPages.size,
            suggestedFeatures = suggestedFeatures,
            requiredSkills = requiredSkills,
            estimatedComplexity = complexity,
            suggestedQuestionsForClient = questions,
            suggestedDeadlineDays = deadlineDays,
            suggestedBudgetMin = budgetMin,
            suggestedBudgetMax = budgetMax,
            generatedDescription = description
        )
    }
}
