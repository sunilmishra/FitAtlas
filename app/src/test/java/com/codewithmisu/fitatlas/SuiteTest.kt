package com.codewithmisu.fitatlas

import com.codewithmisu.fitatlas.body.BodyRepositoryTest
import com.codewithmisu.fitatlas.body.BodyViewModelTest
import com.codewithmisu.fitatlas.exercise.ExerciseListViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    BodyRepositoryTest::class,
    BodyViewModelTest::class,
    ExerciseListViewModelTest::class
)
class SuiteTest
