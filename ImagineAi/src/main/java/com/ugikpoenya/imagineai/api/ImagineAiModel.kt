package com.ugikpoenya.imagineai.api

class ImagineAiModel {
    var prompt: String? = null
    var aspect_ratio: String? = null
    var style_id: Int = 0
    var negative_prompt: String? = null
    var cfg: Float = 0f

    var seed: Int = 0
    var steps: Int = 0
    var high_res_results: Int = 0

    var image: String? = null
    var strength: Int = 0
    var control: String? = null
    var neg_prompt: String? = null

    var mask: String? = null
    var inpaint_strength: Float = 0f

    var model_version: Int = 0

}