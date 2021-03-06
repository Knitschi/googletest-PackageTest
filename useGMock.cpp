
# pragma warning(push)
# pragma warning(disable:4251)
# pragma warning(disable:4275)
#include <gmock/gmock.h>
# pragma warning(pop)

class MyClass
{
public:
    virtual double multiply(double a, double b)
    {
        return a * b;
    }
};

class MyMock : public MyClass
{
public:
	MOCK_METHOD2(multiply, double(double, double));
};

TEST( TestWithMock, TestWithMock)
{
    MyMock mock;
	EXPECT_CALL(mock, multiply(testing::_, testing::_)).WillOnce(testing::Return(5));
    
    EXPECT_EQ( mock.multiply(2,1), 5);
}
