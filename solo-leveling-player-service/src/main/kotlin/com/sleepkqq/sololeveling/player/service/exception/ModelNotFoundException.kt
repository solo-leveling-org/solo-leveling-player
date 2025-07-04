package com.sleepkqq.sololeveling.player.service.exception

import com.sleepkqq.sololeveling.player.model.entity.Model
import kotlin.reflect.KClass

class ModelNotFoundException(klass: KClass<out Model>, id: Any) :
	RuntimeException("${klass.simpleName} not found id=$id")
