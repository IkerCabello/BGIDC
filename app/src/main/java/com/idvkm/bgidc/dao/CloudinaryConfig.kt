package com.idvkm.bgidc.dao

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

object CloudinaryConfig {

    //Upload images to the cloud

    val cloudinary = Cloudinary(ObjectUtils.asMap(
        "cloud_name", "dg2bndeqt",      // Cloud name
        "api_key", "918956928521882",        // API key
        "api_secret", "QmEmTEm2_KAX9zmzTD4tt-x9H8s"   // API secret
    ))
}