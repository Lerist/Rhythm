/*
 * Copyright (C) 2016 Actinarium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actinarium.rhythm.config;

import com.actinarium.rhythm.RhythmSpecLayer;

/**
 * Interface for a factory that can instantiate a {@link RhythmSpecLayer} implementation from provided {@link
 * LayerConfig}. These factories are used by {@link RhythmOverlayInflater} to inflate declarative config into respective
 * overlays. If you make a custom spec layer, you should also create a corresponding <code>RhythmSpecLayerFactory</code>
 * and register it within {@link RhythmOverlayInflater#registerFactory(String, RhythmSpecLayerFactory)}
 * method.<br>Concrete factories may implement some sort of caching and provide the same {@link RhythmSpecLayer}
 * instances for equal {@link LayerConfig}s if they can be reused, but it's not mandatory.
 *
 * @author Paul Danyliuk
 */
public interface RhythmSpecLayerFactory<T extends RhythmSpecLayer> {

    /**
     * Create and configure a spec layer based on provided configuration, or get previously created one from cache if it
     * can be safely reused. There's no need to verify if {@link LayerConfig#getLayerType()} corresponds to this
     * factory.
     *
     * @param config container with arguments for this layer
     * @return configured layer
     */
    T getForConfig(LayerConfig config);

}
