package com.adamratzman.services

import com.adamratzman.utils.fixServiceRoutingWithOptionalBeforeSending

object BlogServiceFrontend  {
    val blogService = BlogService(beforeSend = fixServiceRoutingWithOptionalBeforeSending())
}