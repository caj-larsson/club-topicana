/**
 * Copyright © 2017 Florian Troßbach (trossbach@gmail.com)
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
package com.github.ftrossbach.club_topicana.core;

/**
 * Created by ftr on 10.11.17.
 */
public interface PartitionCount {

    boolean isSpecified();

    int count();


    static PartitionCount of(int count){

        return new SpecifiedCount(count);

    }

    static PartitionCount ignore(){
        return new UnspecifiedCount();
    }


    class SpecifiedCount implements PartitionCount{

        private final int count;

        private SpecifiedCount(int count) {
            if(count <= 0){
                throw new IllegalArgumentException("Count must be larger than 0");
            }
            this.count = count;
        }

        @Override
        public boolean isSpecified() {
            return true;
        }

        @Override
        public int count() {
            return count;
        }


    }

    class UnspecifiedCount implements PartitionCount{


        private UnspecifiedCount(){

        }


        @Override
        public boolean isSpecified() {
            return false;
        }

        @Override
        public int count() {
            throw new IllegalStateException("Must not call count() on an unspecified PartitionCount");
        }
    }


}


