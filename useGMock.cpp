
#include <gmock/gmock.h>

class MyClass
{
public:
    virtual double multiply(double a, double b)
    {
        return a * b;
    }
}

class MyMock : public MyClass
{
public:
    MOCK_METHOD2(multiply, double(double,double))
}

TEST( Test1, Test1)
{
    MyMock mock;
    EXPECT_CALL(mock, multiply).WillOnce(testing::Return(5));

    EXPECT_EQ( mock.multiply(2,1), 5);
}


