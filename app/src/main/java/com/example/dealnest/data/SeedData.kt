package com.example.dealnest.data

import com.example.dealnest.model.*

object SeedData {

    val demoDesigners = listOf(
        User(
            id = "des_1",
            name = "Sarah Jenkins",
            email = "sarah.j@designnest.io",
            role = UserRole.DESIGNER.name,
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
            headline = "Senior Product Designer & Webflow Master",
            handle = "sarahjenkins",
            skills = "Webflow, Figma, Tailwind CSS, Interaction Design, SEO",
            experienceYears = 6,
            portfolioLinks = "https://sarahjenkins.design, https://dribbble.com/sarahj",
            startingPrice = 1200.0,
            availability = "Available this week",
            deliveryTimeDays = 10,
            languages = "English, Spanish",
            about = "Helping seed and Series-A SaaS companies craft conversion-focused websites with smooth micro-interactions and atomic design systems.",
            services = "Webflow Design & Development, Interaction Prototypes, Design Systems, SEO & Performance Tuning",
            isVerified = true,
            location = "San Francisco, CA (Remote)",
            timeZone = "UTC-7 (PST)",
            contactPreference = "In-App Chat",
            rating = 4.95f,
            completedProjectsCount = 38,
            isPro = true
        ),
        User(
            id = "des_2",
            name = "Marcus Chen",
            email = "marcus@chenstudio.co",
            role = UserRole.DESIGNER.name,
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            headline = "Full-Stack Shopify & E-Commerce Developer",
            handle = "marcuschen",
            skills = "Shopify, Next.js, Liquid, TypeScript, Stripe, React",
            experienceYears = 8,
            portfolioLinks = "https://chenstudio.co, https://github.com/marcuschen",
            startingPrice = 1800.0,
            availability = "2 spots left for this month",
            deliveryTimeDays = 14,
            languages = "English, Mandarin",
            about = "Custom high-performance Shopify storefronts with headless architecture that turn casual visitors into loyal repeat buyers.",
            services = "Custom Headless Storefronts, Shopify Liquid Themes, Stripe Checkout Funnels, Speed 95+ Audits",
            isVerified = true,
            location = "Toronto, Canada",
            timeZone = "UTC-5 (EST)",
            contactPreference = "In-App Chat",
            rating = 5.0f,
            completedProjectsCount = 52,
            isPro = true
        ),
        User(
            id = "des_3",
            name = "Elena Rostova",
            email = "elena.design@creativelab.com",
            role = UserRole.DESIGNER.name,
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            headline = "Minimalist Brand & Squarespace/Framer Specialist",
            handle = "elenarostova",
            skills = "Framer, Figma, Brand Identity, Responsive UI, Squarespace",
            experienceYears = 4,
            portfolioLinks = "https://elenarostova.framer.website",
            startingPrice = 850.0,
            availability = "Immediate start",
            deliveryTimeDays = 7,
            languages = "English, French",
            about = "Clean, aesthetic and responsive editorial designs for architecture studios, luxury authors, and boutique lifestyle brands.",
            services = "Framer Interactive Sites, Squarespace Editorial Customization, Brand Style Guides",
            isVerified = false,
            location = "London, UK",
            timeZone = "UTC+1 (BST)",
            contactPreference = "In-App Chat",
            rating = 4.88f,
            completedProjectsCount = 22,
            isPro = false
        ),
        User(
            id = "des_4",
            name = "David Adeleke",
            email = "david@adelekelabs.tech",
            role = UserRole.DESIGNER.name,
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
            headline = "Modern WordPress & Web Application Engineer",
            handle = "davidadeleke",
            skills = "WordPress, Elementor, PHP, React, Node.js, Custom Plugins",
            experienceYears = 5,
            portfolioLinks = "https://davidadeleke.dev",
            startingPrice = 700.0,
            availability = "Available full-time",
            deliveryTimeDays = 8,
            languages = "English",
            about = "Fast, secure and mobile-first websites tailored for small businesses, clinics, and professional agencies with appointment booking systems.",
            services = "Custom WordPress Theme & Plugin Dev, Elementor Pro Builds, Clinic/Salon Booking Workflows",
            isVerified = true,
            location = "Austin, TX",
            timeZone = "UTC-6 (CST)",
            contactPreference = "In-App Chat",
            rating = 4.82f,
            completedProjectsCount = 31,
            isPro = false
        )
    )

    val demoClient = User(
        id = "client_me",
        name = "Jordan Vance",
        email = "jordan@novalabs.io",
        role = UserRole.CLIENT.name,
        avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e",
        headline = "Founder at NovaLabs Ventures",
        handle = "jordanvance",
        skills = "Product Strategy, Growth Marketing",
        experienceYears = 10,
        portfolioLinks = "https://novalabs.io",
        startingPrice = 0.0,
        availability = "Hiring actively",
        deliveryTimeDays = 0,
        languages = "English",
        about = "Building next-generation digital tools and hiring top-tier website designers for our portfolio companies.",
        services = "Seed Venture Funding, Growth Advisory",
        isVerified = true,
        location = "New York, NY",
        timeZone = "UTC-5 (EST)",
        contactPreference = "In-App Chat",
        rating = 4.95f,
        completedProjectsCount = 14,
        isPro = true
    )

    val demoProjects = listOf(
        Project(
            id = "proj_1",
            clientUserId = "client_me",
            clientName = "NovaLabs Ventures",
            clientLocation = "New York, USA",
            title = "Modern E-Commerce Store for Eco-Friendly Apparel",
            websiteType = "E-Commerce",
            description = "We are launching a sustainable bamboo apparel brand and need a modern, high-converting storefront on Shopify or Next.js. Needs custom product builder, carbon footprint calculator, and seamless checkout.",
            budgetMin = 2500.0,
            budgetMax = 4000.0,
            deadlineDays = 21,
            requiredSkills = "Shopify, Next.js, Figma, Tailwind CSS, Stripe",
            pageCount = 8,
            requiredFeatures = "Custom Cart Drawer, Carbon Offset Calculator, Product Filtering, Customer Reviews Integration",
            referenceLinks = "https://allbirds.com, https://patagonia.com",
            status = ProjectStatus.OPEN.name,
            createdAt = System.currentTimeMillis() - 3600000 * 5,
            isFeatured = true
        ),
        Project(
            id = "proj_2",
            clientUserId = "client_2",
            clientName = "Apex Capital Partners",
            clientLocation = "London, UK",
            title = "High-Impact FinTech Landing Page & Interactive Calculator",
            websiteType = "SaaS",
            description = "Need a sleek dark-themed landing page with smooth Framer/Webflow scroll animations, live interest calculator, and lead capture form that integrates into HubSpot.",
            budgetMin = 1800.0,
            budgetMax = 2800.0,
            deadlineDays = 14,
            requiredSkills = "Webflow, Framer, Figma, Interaction Design, HubSpot",
            pageCount = 4,
            requiredFeatures = "Interactive Slider Calculator, Lead Magnet Gate, Mobile-Optimized Performance Score 95+",
            referenceLinks = "https://stripe.com, https://ramp.com",
            status = ProjectStatus.OPEN.name,
            createdAt = System.currentTimeMillis() - 3600000 * 18,
            isFeatured = true
        ),
        Project(
            id = "proj_3",
            clientUserId = "client_3",
            clientName = "Artisan Roast Roastery",
            clientLocation = "Seattle, WA",
            title = "Specialty Coffee Roaster Website & Monthly Subscription",
            websiteType = "Booking",
            description = "Family-owned coffee roaster needs an engaging storytelling website with monthly recurring coffee bag subscriptions, tasting notes quiz, and wholesale inquiry portal.",
            budgetMin = 1200.0,
            budgetMax = 2000.0,
            deadlineDays = 12,
            requiredSkills = "Shopify, Squarespace, SEO, Responsive UI",
            pageCount = 6,
            requiredFeatures = "Subscription Billing, Taste Match Quiz, Cafe Locator Map, Wholesale Form",
            referenceLinks = "https://bluebottlecoffee.com",
            status = ProjectStatus.OPEN.name,
            createdAt = System.currentTimeMillis() - 3600000 * 30,
            isFeatured = false
        ),
        Project(
            id = "proj_4",
            clientUserId = "client_4",
            clientName = "Dr. Stephanie Health Clinic",
            clientLocation = "Chicago, IL",
            title = "Medical & Dental Practice Redesign with Online Booking",
            websiteType = "Corporate",
            description = "Redesign our medical practice website to be warm, ADA-accessible, and integrated with online appointment scheduling and patient intake forms.",
            budgetMin = 1500.0,
            budgetMax = 2400.0,
            deadlineDays = 16,
            requiredSkills = "WordPress, Figma, Responsive UI, Elementor",
            pageCount = 7,
            requiredFeatures = "HIPAA-Friendly Forms, Doctor Bio Pages, Google Maps Clinic Locator, Fast Mobile Speed",
            referenceLinks = "https://onemedical.com",
            status = ProjectStatus.OPEN.name,
            createdAt = System.currentTimeMillis() - 3600000 * 50,
            isFeatured = false
        )
    )

    val demoProposals = listOf(
        Proposal(
            id = "prop_1",
            projectId = "proj_1",
            projectTitle = "Modern E-Commerce Store for Eco-Friendly Apparel",
            designerUserId = "des_2",
            designerName = "Marcus Chen",
            designerHeadline = "Full-Stack Shopify & E-Commerce Developer",
            proposedPrice = 3200.0,
            deliveryDays = 18,
            coverMessage = "Hi Jordan! I've built 50+ headless and Shopify storefronts with sub-second page loads. Your eco-friendly brand story will shine with our custom carbon calculator. Excited to partner on this!",
            portfolioLink = "https://chenstudio.co/portfolio/eco-store",
            status = ProposalStatus.ACCEPTED.name,
            createdAt = System.currentTimeMillis() - 3600000 * 3
        ),
        Proposal(
            id = "prop_2",
            projectId = "proj_1",
            projectTitle = "Modern E-Commerce Store for Eco-Friendly Apparel",
            designerUserId = "des_1",
            designerName = "Sarah Jenkins",
            designerHeadline = "Senior Product Designer & Webflow Master",
            proposedPrice = 2900.0,
            deliveryDays = 15,
            coverMessage = "Hello! Love the concept. I specialize in high-end aesthetic Webflow eCommerce with custom interactions. I can have wireframes ready within 4 days.",
            portfolioLink = "https://sarahjenkins.design/work",
            status = ProposalStatus.PENDING.name,
            createdAt = System.currentTimeMillis() - 3600000 * 4
        ),
        Proposal(
            id = "prop_3",
            projectId = "proj_2",
            projectTitle = "High-Impact FinTech Landing Page & Interactive Calculator",
            designerUserId = "des_3",
            designerName = "Elena Rostova",
            designerHeadline = "Minimalist Brand & Framer Specialist",
            proposedPrice = 2100.0,
            deliveryDays = 12,
            coverMessage = "I build ultra-smooth Framer websites with interactive math components and crisp typography. Perfect match for your FinTech requirements.",
            portfolioLink = "https://elenarostova.framer.website",
            status = ProposalStatus.PENDING.name,
            createdAt = System.currentTimeMillis() - 3600000 * 12
        )
    )

    val demoDeals = listOf(
        Deal(
            id = "deal_1",
            projectId = "proj_1",
            projectTitle = "Modern E-Commerce Store for Eco-Friendly Apparel",
            clientUserId = "client_me",
            clientName = "Jordan Vance (NovaLabs)",
            designerUserId = "des_2",
            designerName = "Marcus Chen",
            agreedPrice = 3200.0,
            deadline = "Oct 28, 2026",
            status = DealStatus.IN_PROGRESS.name,
            milestonesSummary = "4 Structured Milestones (2 Completed, 1 In Review, 1 Pending)",
            completedMilestonesCount = 2,
            totalMilestonesCount = 4,
            sharedFiles = "EcoStore_Wireframes_v2.fig, Product_Taxonomy.xlsx, Shopify_Theme_Assets.zip",
            lastUpdated = System.currentTimeMillis() - 3600000 * 2
        ),
        Deal(
            id = "deal_2",
            projectId = "proj_legacy",
            projectTitle = "SaaS Analytics Dashboard UI Kit",
            clientUserId = "client_me",
            clientName = "Jordan Vance",
            designerUserId = "des_1",
            designerName = "Sarah Jenkins",
            agreedPrice = 1600.0,
            deadline = "Completed Sep 15, 2026",
            status = DealStatus.COMPLETED.name,
            milestonesSummary = "All 3 Milestones Delivered & Approved",
            completedMilestonesCount = 3,
            totalMilestonesCount = 3,
            sharedFiles = "Final_Design_System.fig, Handoff_Notes.md",
            lastUpdated = System.currentTimeMillis() - 86400000 * 9
        ),
        Deal(
            id = "deal_3",
            projectId = "proj_completed_elena",
            projectTitle = "Boutique Editorial Website & Brand Guide",
            clientUserId = "client_me",
            clientName = "Jordan Vance",
            designerUserId = "des_3",
            designerName = "Elena Rostova",
            agreedPrice = 1250.0,
            deadline = "Completed Yesterday",
            status = DealStatus.COMPLETED.name,
            milestonesSummary = "Design System, Framer Pages, SEO Handover (Completed)",
            completedMilestonesCount = 3,
            totalMilestonesCount = 3,
            sharedFiles = "Framer_Export.zip, Typography_Guide.pdf",
            lastUpdated = System.currentTimeMillis() - 86400000 * 1
        )
    )

    val demoMilestones = listOf(
        // For deal_1
        DealMilestone(
            id = "ms_1_1",
            dealId = "deal_1",
            title = "1. Brand Identity & Wireframe Architecture",
            description = "Information architecture, UX wireframes for all 8 core views, and color palette exploration.",
            amount = 600.0,
            deadline = "Oct 04, 2026",
            status = MilestoneStatus.APPROVED.name,
            orderIndex = 1
        ),
        DealMilestone(
            id = "ms_1_2",
            dealId = "deal_1",
            title = "2. High-Fidelity Figma UI Prototypes",
            description = "Component library, product page variations, carbon calculator modal, and mobile design specs.",
            amount = 900.0,
            deadline = "Oct 12, 2026",
            status = MilestoneStatus.APPROVED.name,
            orderIndex = 2
        ),
        DealMilestone(
            id = "ms_1_3",
            dealId = "deal_1",
            title = "3. Shopify Theme Development & Custom Cart",
            description = "Clean Liquid/Next.js code implementation, sliding cart drawer, reviews integration, and API hooks.",
            amount = 1200.0,
            deadline = "Oct 20, 2026",
            status = MilestoneStatus.SUBMITTED.name,
            orderIndex = 3
        ),
        DealMilestone(
            id = "ms_1_4",
            dealId = "deal_1",
            title = "4. QA Testing, Speed Optimization & Handoff",
            description = "Cross-browser QA on iOS/Android, 95+ mobile speed score, DNS domain setup, and staff video guide.",
            amount = 500.0,
            deadline = "Oct 28, 2026",
            status = MilestoneStatus.PENDING.name,
            orderIndex = 4
        ),

        // For deal_2
        DealMilestone(
            id = "ms_2_1",
            dealId = "deal_2",
            title = "1. Design Tokens & Component Library",
            description = "Auto-layout tokens, charts, filters, and dark mode variants.",
            amount = 500.0,
            deadline = "Sep 01, 2026",
            status = MilestoneStatus.COMPLETED.name,
            orderIndex = 1
        ),
        DealMilestone(
            id = "ms_2_2",
            dealId = "deal_2",
            title = "2. Analytics Dashboard Views",
            description = "Revenue metrics, retention cohort tables, and billing view.",
            amount = 600.0,
            deadline = "Sep 08, 2026",
            status = MilestoneStatus.COMPLETED.name,
            orderIndex = 2
        ),
        DealMilestone(
            id = "ms_2_3",
            dealId = "deal_2",
            title = "3. Final Handoff & Developer Specs",
            description = "Exported assets, CSS styling tokens, and React code snippets.",
            amount = 500.0,
            deadline = "Sep 15, 2026",
            status = MilestoneStatus.COMPLETED.name,
            orderIndex = 3
        )
    )

    val demoPortfolioProjects = listOf(
        PortfolioProject(
            id = "port_1",
            designerUserId = "des_2",
            title = "Solaria Sustainable Living",
            category = "E-Commerce",
            description = "Custom headless Shopify storefront with sub-second page loads and carbon emissions calculator.",
            techStack = "Shopify, Next.js, Stripe, Tailwind CSS",
            liveUrl = "https://solaria-demo.com",
            gradientColorIndex = 0,
            metricsHighlight = "+140% Mobile Conversions • 98 Lighthouse Score"
        ),
        PortfolioProject(
            id = "port_2",
            designerUserId = "des_2",
            title = "Onyx Coffee Roasters",
            category = "Subscription Web",
            description = "Dynamic monthly recurring roast subscription quiz and wholesale procurement portal.",
            techStack = "Shopify Liquid, Vue.js, ReCharge",
            liveUrl = "https://onyx-coffee-demo.com",
            gradientColorIndex = 1,
            metricsHighlight = "4.2x Recurring Subscriptions • $1.2M GMV"
        ),
        PortfolioProject(
            id = "port_3",
            designerUserId = "des_1",
            title = "Luminary AI Platform",
            category = "SaaS Landing",
            description = "Futuristic dark-themed Webflow marketing site with 3D canvas micro-interactions.",
            techStack = "Webflow, Spline 3D, GSAP",
            liveUrl = "https://luminary-ai.io",
            gradientColorIndex = 2,
            metricsHighlight = "Site of the Day • 28% Lead Capture"
        ),
        PortfolioProject(
            id = "port_4",
            designerUserId = "des_1",
            title = "Vanguard Wealth Management",
            category = "Corporate FinTech",
            description = "High-security financial advisor directory with real-time portfolio performance graphing.",
            techStack = "Figma, React, Tailwind, Chart.js",
            liveUrl = "https://vanguard-wealth.design",
            gradientColorIndex = 3,
            metricsHighlight = "A11y ADA Certified • 60k Monthly Users"
        ),
        PortfolioProject(
            id = "port_5",
            designerUserId = "des_3",
            title = "Atelier Monolith Architecture",
            category = "Portfolio / Editorial",
            description = "Minimalist masonry gallery featuring high-res architectural renders and inquiry system.",
            techStack = "Framer, Figma, Typography",
            liveUrl = "https://monolith-arch.framer.website",
            gradientColorIndex = 4,
            metricsHighlight = "Design Awards Nominee • 0.4s FCP"
        )
    )

    val demoLeadAlertConfigs = listOf(
        DesignerLeadAlertConfig(
            designerUserId = "des_2",
            monitoredSkills = "Shopify, Next.js, Stripe, E-Commerce",
            minBudget = 1500.0,
            preferredWebsiteTypes = "E-Commerce, Booking, SaaS",
            isEnabled = true
        ),
        DesignerLeadAlertConfig(
            designerUserId = "des_1",
            monitoredSkills = "Webflow, Figma, Tailwind CSS, SaaS",
            minBudget = 1200.0,
            preferredWebsiteTypes = "SaaS, Landing Page, Corporate",
            isEnabled = true
        )
    )

    val demoConversations = listOf(
        Conversation(
            id = "conv_1",
            projectId = "proj_1",
            projectTitle = "Modern E-Commerce Store for Eco-Friendly Apparel",
            clientUserId = "client_me",
            clientName = "Jordan Vance",
            clientAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e",
            designerUserId = "des_2",
            designerName = "Marcus Chen",
            designerAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            designerHeadline = "Full-Stack Shopify & E-Commerce Developer",
            dealId = "deal_1",
            dealStatus = "DEAL_ACTIVE",
            agreedPrice = 3200.0,
            budget = "$2,500 - $4,000",
            deadline = "14 days",
            projectStatus = "IN_PROGRESS",
            lastMessageText = "Awesome! I've kicked off the Shopify custom theme build. Will share the preview staging link by Friday!",
            lastMessageSenderId = "des_2",
            lastMessageTimestamp = System.currentTimeMillis() - 3600000 * 1,
            unreadCountClient = 1,
            unreadCountDesigner = 0,
            isDesignerOnline = true,
            isClientOnline = true,
            designerLastSeen = "Online",
            clientLastSeen = "Online"
        ),
        Conversation(
            id = "conv_2",
            projectId = "proj_3",
            projectTitle = "SaaS Analytics Dashboard UI/UX Redesign",
            clientUserId = "client_me",
            clientName = "Jordan Vance",
            clientAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e",
            designerUserId = "des_1",
            designerName = "Sarah Jenkins",
            designerAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
            designerHeadline = "Senior Product Designer & Webflow Master",
            dealId = "deal_2",
            dealStatus = "DEAL_ACTIVE",
            agreedPrice = 4500.0,
            budget = "$3,500 - $5,000",
            deadline = "28 days",
            projectStatus = "IN_PROGRESS",
            lastMessageText = "The component library in Figma is 100% prepared with tokenized variables.",
            lastMessageSenderId = "des_1",
            lastMessageTimestamp = System.currentTimeMillis() - 3600000 * 6,
            unreadCountClient = 0,
            unreadCountDesigner = 0,
            isDesignerOnline = true,
            isClientOnline = true,
            designerLastSeen = "Online",
            clientLastSeen = "Online"
        ),
        Conversation(
            id = "conv_3",
            projectId = "proj_4",
            projectTitle = "Minimalist Architecture Portfolio & Brand Book",
            clientUserId = "client_me",
            clientName = "Jordan Vance",
            clientAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e",
            designerUserId = "des_3",
            designerName = "Elena Rostova",
            designerAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            designerHeadline = "Minimalist Brand & Squarespace/Framer Specialist",
            dealId = null,
            dealStatus = null,
            agreedPrice = null,
            budget = "$1,800 - $2,600",
            deadline = "14 days",
            projectStatus = "OPEN",
            lastMessageText = "I've submitted a proposal with Framer interactive animations. Excited to discuss the layout details!",
            lastMessageSenderId = "des_3",
            lastMessageTimestamp = System.currentTimeMillis() - 3600000 * 12,
            unreadCountClient = 2,
            unreadCountDesigner = 0,
            isDesignerOnline = false,
            isClientOnline = true,
            designerLastSeen = "Active 20m ago",
            clientLastSeen = "Online"
        ),
        Conversation(
            id = "conv_4",
            projectId = "proj_5",
            projectTitle = "Dental Clinic Booking Website & CRM Integration",
            clientUserId = "client_me",
            clientName = "Jordan Vance",
            clientAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e",
            designerUserId = "des_4",
            designerName = "David Adeleke",
            designerAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
            designerHeadline = "Modern WordPress & Web Application Engineer",
            dealId = null,
            dealStatus = null,
            agreedPrice = null,
            budget = "$1,500 - $2,200",
            deadline = "10 days",
            projectStatus = "OPEN",
            lastMessageText = "Hi Jordan! I checked the clinic booking requirements. We can integrate Cal.com or Calendly directly with HIPAA-compliant forms.",
            lastMessageSenderId = "des_4",
            lastMessageTimestamp = System.currentTimeMillis() - 86400000,
            unreadCountClient = 0,
            unreadCountDesigner = 0,
            isDesignerOnline = true,
            isClientOnline = true,
            designerLastSeen = "Online",
            clientLastSeen = "Online"
        )
    )

    val demoMessages = listOf(
        ChatMessage(
            id = "msg_1",
            dealOrChannelId = "conv_1",
            senderUserId = "des_2",
            senderName = "Marcus Chen",
            senderRole = UserRole.DESIGNER.name,
            text = "Hey Jordan! I've uploaded the completed high-fidelity mockups for the product page and cart drawer. Take a look when you have a moment.",
            attachmentName = "EcoStore_HiFi_Mockups_v1.fig",
            attachmentType = "FIGMA",
            timestamp = System.currentTimeMillis() - 3600000 * 4,
            isRead = true
        ),
        ChatMessage(
            id = "msg_2",
            dealOrChannelId = "conv_1",
            senderUserId = "client_me",
            senderName = "Jordan Vance",
            senderRole = UserRole.CLIENT.name,
            text = "These look phenomenal Marcus! The carbon savings badge in the cart drawer is spot on. Let's move directly to milestone 3 development.",
            attachmentName = null,
            replyToMessageId = "msg_1",
            replyToText = "Hey Jordan! I've uploaded the completed high-fidelity mockups...",
            replyToSenderName = "Marcus Chen",
            timestamp = System.currentTimeMillis() - 3600000 * 3,
            isRead = true
        ),
        ChatMessage(
            id = "msg_3",
            dealOrChannelId = "conv_1",
            senderUserId = "des_2",
            senderName = "Marcus Chen",
            senderRole = UserRole.DESIGNER.name,
            text = "Awesome! I've kicked off the Shopify custom theme build. Will share the preview staging link by Friday!",
            attachmentName = "Storefront_Preview_v1.png",
            attachmentType = "IMAGE",
            timestamp = System.currentTimeMillis() - 3600000 * 1,
            isRead = false
        ),
        ChatMessage(
            id = "msg_deal_1",
            dealOrChannelId = "deal_1",
            senderUserId = "des_2",
            senderName = "Marcus Chen",
            senderRole = UserRole.DESIGNER.name,
            text = "Hey Jordan! High-fidelity mockups and milestone 2 updates are ready in the Deal Room.",
            attachmentName = "EcoStore_HiFi_Mockups_v1.fig",
            attachmentType = "FIGMA",
            timestamp = System.currentTimeMillis() - 3600000 * 4,
            isRead = true
        ),
        ChatMessage(
            id = "msg_4",
            dealOrChannelId = "conv_2",
            senderUserId = "client_me",
            senderName = "Jordan Vance",
            senderRole = UserRole.CLIENT.name,
            text = "Hi Sarah, how is the design system progressing for the SaaS dashboard?",
            timestamp = System.currentTimeMillis() - 3600000 * 7,
            isRead = true
        ),
        ChatMessage(
            id = "msg_5",
            dealOrChannelId = "conv_2",
            senderUserId = "des_1",
            senderName = "Sarah Jenkins",
            senderRole = UserRole.DESIGNER.name,
            text = "The component library in Figma is 100% prepared with tokenized variables. Exporting the specs now!",
            attachmentName = "Design_System_Tokens.pdf",
            attachmentType = "DOCUMENT",
            timestamp = System.currentTimeMillis() - 3600000 * 6,
            isRead = true
        ),
        ChatMessage(
            id = "msg_6",
            dealOrChannelId = "conv_3",
            senderUserId = "des_3",
            senderName = "Elena Rostova",
            senderRole = UserRole.DESIGNER.name,
            text = "Hello Jordan! I've submitted a proposal for the Architecture Portfolio. Would love to show you some similar editorial layouts I built in Framer.",
            attachmentName = "Minimalist_Portfolio_Deck.pdf",
            attachmentType = "DOCUMENT",
            timestamp = System.currentTimeMillis() - 3600000 * 12,
            isRead = false
        ),
        ChatMessage(
            id = "msg_7",
            dealOrChannelId = "conv_3",
            senderUserId = "des_3",
            senderName = "Elena Rostova",
            senderRole = UserRole.DESIGNER.name,
            text = "I've submitted a proposal with Framer interactive animations. Excited to discuss the layout details!",
            timestamp = System.currentTimeMillis() - 3600000 * 11,
            isRead = false
        ),
        ChatMessage(
            id = "msg_8",
            dealOrChannelId = "conv_4",
            senderUserId = "des_4",
            senderName = "David Adeleke",
            senderRole = UserRole.DESIGNER.name,
            text = "Hi Jordan! I checked the clinic booking requirements. We can integrate Cal.com or Calendly directly with HIPAA-compliant forms.",
            timestamp = System.currentTimeMillis() - 86400000,
            isRead = true
        )
    )

    val demoReviews = listOf(
        Review(
            id = "rev_1",
            dealId = "deal_2",
            targetUserId = "des_1",
            authorUserId = "client_me",
            authorName = "Jordan Vance",
            authorRole = "Client",
            rating = 5,
            reviewText = "Sarah is exceptional. Her attention to detail and proactive communication made this dashboard project effortless. High conversion numbers already!",
            timestamp = System.currentTimeMillis() - 86400000 * 8
        ),
        Review(
            id = "rev_2",
            dealId = "deal_2",
            targetUserId = "client_me",
            authorUserId = "des_1",
            authorName = "Sarah Jenkins",
            authorRole = "Designer",
            rating = 5,
            reviewText = "Jordan was a dream client. Clear scope, prompt feedback on revisions, and fast milestone approvals. Would love to collaborate again!",
            timestamp = System.currentTimeMillis() - 86400000 * 8
        ),
        Review(
            id = "rev_3",
            dealId = "deal_old_1",
            targetUserId = "des_2",
            authorUserId = "client_ext_1",
            authorName = "Elena Gomez (Solaria)",
            authorRole = "Client",
            rating = 5,
            reviewText = "Marcus delivered our Shopify redesign 3 days ahead of schedule. Our store speed went from 48 to 96 on mobile!",
            timestamp = System.currentTimeMillis() - 86400000 * 20
        )
    )

    val demoNotifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "New Proposal Received!",
            description = "Marcus Chen submitted a proposal of $3,200 for 'Modern E-Commerce Store'.",
            timestamp = System.currentTimeMillis() - 3600000 * 5,
            isRead = false,
            type = "PROPOSAL"
        ),
        AppNotification(
            id = "notif_2",
            title = "Deal Milestone Progress",
            description = "Milestone 2 for 'Modern E-Commerce Store' marked complete.",
            timestamp = System.currentTimeMillis() - 3600000 * 2,
            isRead = false,
            type = "DEAL"
        ),
        AppNotification(
            id = "notif_3",
            title = "Smart Match Found!",
            description = "A new project matching your Webflow skills was posted with budget $2,800.",
            timestamp = System.currentTimeMillis() - 3600000 * 12,
            isRead = true,
            type = "MATCH"
        ),
        AppNotification(
            id = "notif_4",
            title = "Milestone Submitted for Review",
            description = "Marcus Chen submitted Milestone 3 (Shopify Theme Dev) for client inspection.",
            timestamp = System.currentTimeMillis() - 3600000 * 1,
            isRead = false,
            type = "MILESTONE"
        ),
        AppNotification(
            id = "notif_5",
            title = "Verification Request Received",
            description = "Elena Rostova submitted a verification application for admin review.",
            timestamp = System.currentTimeMillis() - 86400000 * 2,
            isRead = false,
            type = "VERIFICATION"
        )
    )

    val demoVerificationRequests = listOf(
        VerificationRequest(
            id = "ver_req_1",
            designerUserId = "des_3",
            designerName = "Elena Rostova",
            fullName = "Elena Rostova",
            email = "elena.design@creativelab.com",
            phone = "+44 20 7946 0912",
            professionalRole = "Minimalist Brand & Squarespace/Framer Specialist",
            skills = "Framer, Figma, Brand Identity, Responsive UI, Squarespace",
            portfolioUrl = "https://elenarostova.framer.website",
            description = "4+ years of professional boutique agency experience in London. Certified Framer Expert with 22 successfully delivered client projects.",
            status = "PENDING",
            submittedAt = System.currentTimeMillis() - 86400000 * 2
        ),
        VerificationRequest(
            id = "ver_req_2",
            designerUserId = "des_2",
            designerName = "Marcus Chen",
            fullName = "Marcus Chen",
            email = "marcus@chenstudio.co",
            phone = "+1 416 555 0198",
            professionalRole = "Full-Stack Shopify & E-Commerce Developer",
            skills = "Shopify, Next.js, Liquid, TypeScript, Stripe, React",
            portfolioUrl = "https://chenstudio.co",
            description = "Official Shopify Partner with over 50 custom storefront builds, 8 years commercial development experience, and proven track record of speed optimization.",
            status = "APPROVED",
            submittedAt = System.currentTimeMillis() - 86400000 * 30,
            reviewedAt = System.currentTimeMillis() - 86400000 * 28
        ),
        VerificationRequest(
            id = "ver_req_3",
            designerUserId = "des_4",
            designerName = "David Adeleke",
            fullName = "David Adeleke",
            email = "david@adelekelabs.tech",
            phone = "+234 802 555 1234",
            professionalRole = "Modern WordPress & Web Application Engineer",
            skills = "WordPress, Elementor, PHP, React, Node.js, Custom Plugins",
            portfolioUrl = "https://davidadeleke.dev",
            description = "5 years specialized WordPress engineering. Built customized booking and payment portals for clinics, legal firms, and universities.",
            status = "APPROVED",
            submittedAt = System.currentTimeMillis() - 86400000 * 45,
            reviewedAt = System.currentTimeMillis() - 86400000 * 40
        )
    )

    val demoDesignerServices = listOf(
        DesignerService(
            id = "srv_1",
            designerUserId = "des_2",
            serviceName = "Custom Shopify Storefront Build",
            description = "Full custom Liquid or Headless Next.js storefront with cart drawer, mobile speed tuning (90+), and payment integration.",
            startingPrice = 1800.0,
            deliveryDays = 14
        ),
        DesignerService(
            id = "srv_2",
            designerUserId = "des_2",
            serviceName = "Shopify Speed & Conversion Audit",
            description = "Deep dive audit of code, apps, assets, and checkout flow to boost mobile conversion rates and drop load time under 1.5s.",
            startingPrice = 450.0,
            deliveryDays = 3
        ),
        DesignerService(
            id = "srv_3",
            designerUserId = "des_1",
            serviceName = "Webflow SaaS Marketing Website",
            description = "Custom 5-page Webflow build with responsive layouts, tokenized Figma design system, and custom GSAP micro-interactions.",
            startingPrice = 1200.0,
            deliveryDays = 10
        ),
        DesignerService(
            id = "srv_4",
            designerUserId = "des_1",
            serviceName = "Figma Design System & UI Kit",
            description = "Atomic component library with auto-layout, dark/light variants, and developer handoff documentation.",
            startingPrice = 800.0,
            deliveryDays = 7
        ),
        DesignerService(
            id = "srv_5",
            designerUserId = "des_3",
            serviceName = "Framer Editorial Portfolio Site",
            description = "Striking visual showcase for studios, architects, and creatives with custom typography, CMS, and smooth page transitions.",
            startingPrice = 850.0,
            deliveryDays = 7
        )
    )

    val demoDisputes = listOf(
        DisputeItem(
            id = "disp_1",
            dealId = "deal_1",
            projectTitle = "Brand Website Redesign & Custom Shopify Store",
            raisedByUserId = "client_1",
            raisedByName = "Sophia Laurent",
            raisedByRole = "CLIENT",
            againstUserId = "des_2",
            againstUserName = "Marcus Chen",
            milestoneTitle = "Milestone 3: Headless Next.js Frontend Integration",
            reason = "Scope disagreement",
            description = "Third-party ERP inventory sync was discussed during project scoping but designer believes it requires additional hours.",
            evidenceNotes = "Refer to chat messages on Sep 22 outlining real-time stock sync requirements.",
            status = "Under Review",
            adminDecisionNotes = "Admin reviewing chat logs and milestone scope sheet. Compromise suggested: Include basic API sync, defer complex multi-warehouse logic to v2.",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            updatedAt = System.currentTimeMillis() - 86400000L
        ),
        DisputeItem(
            id = "disp_2",
            dealId = "deal_2",
            projectTitle = "FinTech SaaS Web App & Marketing Site",
            raisedByUserId = "des_1",
            raisedByName = "Sarah Jenkins",
            raisedByRole = "DESIGNER",
            againstUserId = "client_demo",
            againstUserName = "Alex Rivera",
            milestoneTitle = "Milestone 4: Final Staging QA & Launch",
            reason = "Missed deadline for client feedback",
            description = "Client has delayed approving Milestone 3 feedback for 12 days, blocking contract finalization.",
            evidenceNotes = "Three reminder notifications sent without response.",
            status = "Resolved",
            adminDecisionNotes = "Client contacted by DealNest mediation. Feedback provided within 24 hours. Milestone 3 approved and payout released.",
            timestamp = System.currentTimeMillis() - 86400000L * 7,
            updatedAt = System.currentTimeMillis() - 86400000L * 3
        )
    )

    val demoPaymentTransactions = listOf(
        PaymentTransaction(
            id = "txn_1",
            dealId = "deal_1",
            milestoneId = "m_1",
            projectTitle = "Brand Website Redesign & Custom Shopify Store",
            payerUserId = "client_1",
            payerName = "Sophia Laurent",
            payeeUserId = "des_2",
            payeeName = "Marcus Chen",
            amount = 1200.0,
            currency = "USD",
            status = "Paid",
            paymentMethod = "Escrow Demo",
            transactionRef = "TXN-DN-2026-09101",
            timestamp = System.currentTimeMillis() - 86400000L * 10
        ),
        PaymentTransaction(
            id = "txn_2",
            dealId = "deal_1",
            milestoneId = "m_2",
            projectTitle = "Brand Website Redesign & Custom Shopify Store",
            payerUserId = "client_1",
            payerName = "Sophia Laurent",
            payeeUserId = "des_2",
            payeeName = "Marcus Chen",
            amount = 1400.0,
            currency = "USD",
            status = "Processing",
            paymentMethod = "Escrow Demo",
            transactionRef = "TXN-DN-2026-09184",
            timestamp = System.currentTimeMillis() - 86400000L * 2
        ),
        PaymentTransaction(
            id = "txn_3",
            dealId = "deal_2",
            milestoneId = "m_4",
            projectTitle = "FinTech SaaS Web App & Marketing Site",
            payerUserId = "client_demo",
            payerName = "Alex Rivera",
            payeeUserId = "des_1",
            payeeName = "Sarah Jenkins",
            amount = 1800.0,
            currency = "USD",
            status = "Paid",
            paymentMethod = "Escrow Demo",
            transactionRef = "TXN-DN-2026-09042",
            timestamp = System.currentTimeMillis() - 86400000L * 15
        )
    )
}
